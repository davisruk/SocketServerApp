package uk.co.boots.osr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.persistence.ToteService;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.MessageResponseHandler;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class TrackController implements ToteEventHandler {

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired 
	private ToteController toteController;
	@Autowired
	private ToteService toteService;

	@Getter
	private int activeTotes;
	
	private SendClientSocketHandler client;
	
	private boolean stopTrackController = false;
	
	@Async
	public void handleClientSocketConnection(SendClientSocketHandler sendClient) {
		System.out.println("[Message Sender] Handling client messages");
		client = sendClient;
		System.out.println("Track controller started");
		int maxTotes = osrBuffer.getTrackToteCapacity();
		System.out.println("osrBuffer Started");
		// osrBuffer needs to be releasing totes - wait if not 
		int totesProcessed = 0;
		while (!stopTrackController) {
			// wait until OSR is releasing and track has availability 
			while (!osrBuffer.isReleasing() || activeTotes == maxTotes);			
			// start tote on track
			Tote t = toteService.getToteInQueuePosition(totesProcessed);
			if (t != null) {
				toteController.releaseTote(t, this, client);
				totesProcessed++;
			}
			try {
				Thread.sleep(osrBuffer.getToteReleaseInterval());
			} catch (InterruptedException ie) {
				System.out.println("This shouldn't happen");
			}
		}
	}

	public void notifyClientOrderPersisted(Tote t) {
		toteService.notifyClientOrderPersisted(t, client);
	}
	
	private synchronized void incrementActiveTotes () {
		activeTotes++;
	}
	
	private synchronized void decrementActiveTotes () {
		activeTotes--;
	}

	@Override
	public void handleToteActivation(Tote tote) {
		incrementActiveTotes();
		
	}

	@Override
	public void handleToteDeactivation(Tote tote) {
		decrementActiveTotes();
	}
}
