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
		OSRConfig config = osrBuffer.getOsrConfig(); 
		int maxTotes = config.getMaxTotesOnTrack();
		System.out.println("osrBuffer Started");
		// write 32R messages in loop
		// osrBuffer needs to be releasing totes - wait if not 
		
		int pageIndex = 0, totesProcessed = 0;
		// get enough Totes from the database to fit on track
		while (!stopTrackController) {
			while (!config.isReleasing())
				;			
			Page<Tote> page;
			if ((page = toteService.getTotePage(pageIndex, 1)).getNumberOfElements() > 0) {
				System.out.println("Page number:" + pageIndex + " Number of entries: " + page.getNumberOfElements());
				pageIndex++;
				page.forEach(t -> {
					while (!config.isReleasing())
						;			
					// make sure we don't release too many totes at once
					while(activeTotes == maxTotes);
					// send 32R short
					// start tote on track
					toteController.releaseTote(t, this, client);
					try {
						Thread.sleep(config.getToteReleaseInterval());
					} catch (InterruptedException ie) {
						System.out.println("This shouldn't happen");
					}
				});
				
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
