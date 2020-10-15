package uk.co.boots.dsp.wcs.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
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
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	private int activeTotes;
	private boolean stopTrackController = false;
	private int totesProcessed = 0;
	
	
	@Async
	public void start() {
		setStopTrackController(false);
		adjustTotesProcessed(true, false);
		adjustActiveTotes(true, false);
		dspEventNotifier.resetHandlers();		
		dspEventNotifier.registerEventHandler(eventLogger);
		dspEventNotifier.registerEventHandler(new ToteActivationHandler());
		dspEventNotifier.registerEventHandler(new ToteFinishedHandler());
		dspEventNotifier.registerEventHandler(new OrderPersistedHandler());
		int maxTotes = osrBuffer.getTrackToteCapacity();
		long releaseInterval = osrBuffer.getToteReleaseInterval();
		logger.info("[TrackController::start] Track Controller Started");
		// osrBuffer needs to be releasing totes - wait if not 
		while (!isStopTrackController()) {
			// wait until OSR is releasing and track has availability 
			if (osrBuffer.isReleasing() && getActiveTotes() < maxTotes) {
				// start tote on track
				long totesInOSR = toteService.getToteCount();
				if (totesInOSR > 0 & totesProcessed < totesInOSR) {
					Tote t = toteService.getToteInQueuePosition(totesProcessed);
					
					logger.info("[TrackController::start] processing tote " + (totesProcessed + 1) + " of " + totesInOSR);
					toteController.releaseTote(t);
					adjustTotesProcessed(false, true);
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
					adjustActiveTotes(false,  true);
					break;
				case TOTE_DEACTIVATED:
					adjustActiveTotes(false,  false);
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
	
	private synchronized int getActiveTotes() {
		return activeTotes;
	}
	
	private synchronized void adjustActiveTotes(boolean reset, boolean increment) {
		if (reset) activeTotes = 0;
		else if (increment) activeTotes ++;
		else activeTotes --;
		logger.info("[TrackController::adjustActiveTotes] Active Totes: " + activeTotes);		
	}
	
	public synchronized void adjustTotesProcessed (boolean reset, boolean increment) {
		if (reset) totesProcessed = 0;
		else if (increment) totesProcessed ++;
		else totesProcessed --;
		logger.info("[TrackController::adjustTotesProcessed] totesProcessed: " + totesProcessed);		
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
