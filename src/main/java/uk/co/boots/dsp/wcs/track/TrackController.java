package uk.co.boots.dsp.wcs.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.events.DSPEventHandlerAdapter;
import uk.co.boots.dsp.wcs.events.DSPEventNotifier;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.events.ToteEvent.EventType;
import uk.co.boots.dsp.wcs.osr.OSRBuffer;
import uk.co.boots.dsp.wcs.service.ToteService;

@Component
public class TrackController {

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired 
	private ToteController toteController;
	@Autowired
	private ToteService toteService;
	@Autowired
    @Qualifier("dspEventNotifier")	
	private DSPEventNotifier dspEventNotifier;
	@Autowired
	TrackStatus trackStatus;
	
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	private boolean stopTrackController = false;

	@Async
	public void start() {
		setStopTrackController(false);
		sendReset();
		logger.info("[TrackController::start] Track Controller Started");
		// osrBuffer needs to be releasing totes - wait if not 
		while (!isStopTrackController()) {
			// wait until OSR is releasing and track has availability 
			if (osrBuffer.isReleasing() && trackStatus.getActiveTotes() < osrBuffer.getTrackToteCapacity()) {
				// start tote on track
				int totesInOSR = trackStatus.getTotalTotes();
				int totesReleased = trackStatus.getTotesReleased();
				if (totesInOSR > 0 && totesReleased < totesInOSR) {
					Tote t = toteService.getToteInQueuePosition(totesReleased);
					logger.debug("[TrackController::start] processing tote " + (totesReleased + 1) + " of " + totesInOSR);
					dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_ACTIVATED, t));
					dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_RELEASED_FROM_OSR, t));
					toteController.releaseTote(t);
					try {
						Thread.sleep(osrBuffer.getToteReleaseInterval());
					} catch (InterruptedException ie) {
						logger.info("[Track Controller::start] Interrupted - resuming");
					}
				}
			}
		}
	}
	
	public synchronized void resetTrackController () {
		sendReset();
		setStopTrackController(true);
	}
	
	private void sendReset() {
		ToteEvent te = new ToteEvent(EventType.RESET_RUN, null);
		dspEventNotifier.notifyEventHandlers(te);
	}
	private synchronized void setStopTrackController (boolean val) {
		stopTrackController = val;
	}

	private synchronized boolean isStopTrackController () {
		return stopTrackController;
	}
	
}
