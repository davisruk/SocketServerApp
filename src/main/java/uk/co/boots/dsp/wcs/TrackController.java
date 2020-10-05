package uk.co.boots.dsp.wcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.events.DSPEventHandler;
import uk.co.boots.dsp.events.DSPEventNotifier;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.wcs.events.ToteEvent;
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

	@Getter
	private int activeTotes;
	
	private boolean stopTrackController = false;
	
	@Async
	public void start() {
		dspEventNotifier.registerEventHandler(new ToteActivationHandler());
		dspEventNotifier.registerEventHandler(new ToteFinishedHandler());
		dspEventNotifier.registerEventHandler(new OrderPersistedHandler());
		System.out.println("[Message Sender] Handling client messages");
		System.out.println("Track controller started");
		int maxTotes = osrBuffer.getTrackToteCapacity();
		long releaseInterval = osrBuffer.getToteReleaseInterval();
		System.out.println("osrBuffer Started");
		// osrBuffer needs to be releasing totes - wait if not 
		int totesProcessed = 0;
		while (!stopTrackController) {
			// wait until OSR is releasing and track has availability 
			if (osrBuffer.isReleasing() && activeTotes < maxTotes) {
				// start tote on track
				Tote t = toteService.getToteInQueuePosition(totesProcessed);
				toteController.releaseTote(t);
				totesProcessed++;
				try {
					Thread.sleep(releaseInterval);
				} catch (InterruptedException ie) {
					System.out.println("This shouldn't happen");
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
					incrementActiveTotes();
					break;
				case TOTE_DEACTIVATED:
					decrementActiveTotes();
					break;
				default:
					break;
			}
		}
	}
	
	private class OrderPersistedHandler implements DSPEventHandler {
		public void handleEvent(ToteEvent event) {
			// TODO Auto-generated method stub
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
				DSPCommsMessage msg = toteService.processToteFinished(t);
				dspCommunicationNotifier.notifyCommunicationHandlers(msg);
				toteService.save(t);
			}
		}
	}
	
	private synchronized void incrementActiveTotes () {
		activeTotes++;
		System.out.println("[Tote Activated] Active Totes: " + activeTotes);
	}
	
	private synchronized void decrementActiveTotes () {
		activeTotes--;
		System.out.println("[Tote De-Activated] Active Totes: " + activeTotes);		
	}

}
