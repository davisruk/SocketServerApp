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
import uk.co.boots.dsp.wcs.events.DSPEventHandler;
import uk.co.boots.dsp.wcs.events.DSPEventNotifier;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;
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
	@Qualifier("dspCommunicationNotifier")
	private DSPCommunicationNotifier dspCommunicationNotifier;
	@Autowired
	private EventLogger eventLogger;
	@Autowired
	TrackStatus trackStatus;
	@Autowired
	WebSocketController webSocketController; // re-factor this out
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	private boolean stopTrackController = false;

	@Async
	public void start() {
		setStopTrackController(false);
		trackStatus.adjustTotesProcessed(true, false);
		trackStatus.adjustActiveTotes(true, false);
		dspEventNotifier.resetHandlers();		
		dspEventNotifier.registerEventHandler(eventLogger);
		dspEventNotifier.registerEventHandler(new ToteActivationHandler());
		dspEventNotifier.registerEventHandler(new ToteFinishedHandler());
		dspEventNotifier.registerEventHandler(new OrderPersistedHandler());
		dspEventNotifier.registerEventHandler(webSocketController);
		int maxTotes = osrBuffer.getTrackToteCapacity();
		long releaseInterval = osrBuffer.getToteReleaseInterval();
		logger.info("[TrackController::start] Track Controller Started");
		// osrBuffer needs to be releasing totes - wait if not 
		while (!isStopTrackController()) {
			// wait until OSR is releasing and track has availability 
			if (osrBuffer.isReleasing() && trackStatus.getActiveTotes() < maxTotes) {
				// start tote on track
				int totesInOSR = trackStatus.getTotalTotes();
				int totesProcessed = trackStatus.getTotesProcessed();
				if (totesInOSR > 0 & totesProcessed < totesInOSR) {
					Tote t = toteService.getToteInQueuePosition(totesProcessed);
					logger.info("[TrackController::start] processing tote " + (totesProcessed + 1) + " of " + totesInOSR);
					toteController.releaseTote(t);
					trackStatus.adjustTotesProcessed(false, true);
					try {
						Thread.sleep(releaseInterval);
					} catch (InterruptedException ie) {
						logger.info("[Track Controller::start] Interrupted - resuming");
					}
				}
			}
		}
	}

	private class ToteActivationHandler implements DSPEventHandler {
		@Override
		public void handleEvent(ToteEvent event) {
			// TODO Auto-generated method stub
			switch (event.getEventType()) {
				case TOTE_ACTIVATED:
					trackStatus.adjustActiveTotes(false,  true);
					break;
				case TOTE_DEACTIVATED:
					trackStatus.adjustActiveTotes(false,  false);
					break;
				default:
					break;
			}
		}
	}
	
	private class OrderPersistedHandler implements DSPEventHandler {
		public void handleEvent(ToteEvent event) {
			if (! osrBuffer.sendThirtyTwoRShort()) return;
			if (event.getEventType() == ToteEvent.EventType.TOTE_ORDER_PERSISTED) {
				Tote t = event.getTote();
				DSPCommsMessage msg = toteService.processClientOrderPersisted(t);
				dspCommunicationNotifier.notifyCommunicationHandlers(msg);
				toteService.save(t);
			}
		}
	}
	
	private class ToteFinishedHandler implements DSPEventHandler {
		@Override
		public void handleEvent(ToteEvent event) {
			if (event.getEventType() == ToteEvent.EventType.TOTE_RELEASED_FOR_DELIVERY) {
				Tote t = event.getTote();
				logger.info("[ToteFinishedHandler::handleEvent] Tote Finished");
				DSPCommsMessage msg = toteService.processToteFinished(t);
				dspCommunicationNotifier.notifyCommunicationHandlers(msg);
				toteService.save(t);
				logger.info("[ToteFinishedHandler::handleEvent] Tote Saved");
			}
		}
	}
	
	public synchronized void resetTrackController () {
		setStopTrackController(true);
	}
	
	private synchronized void setStopTrackController (boolean val) {
		stopTrackController = val;
	}

	private synchronized boolean isStopTrackController () {
		return stopTrackController;
	}
	
}
