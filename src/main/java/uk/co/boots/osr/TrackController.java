package uk.co.boots.osr;

import java.util.ArrayDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class TrackController implements ToteEventHandler {

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired ToteController toteController;
	
	@Getter
	private int activeTotes;

	private SendClientSocketHandler client;

	@Async
	public void start(SendClientSocketHandler client) {
		this.client = client;
		System.out.println("Track controller started");
		OSRConfig config = osrBuffer.getOsrConfig(); 
		while (!config.isReleasing())
			;
		int maxTotes = config.getMaxTotesOnTrack();
		System.out.println("osrBuffer Started");
		// write 32R messages in loop
		ArrayDeque<String> totes = osrBuffer.getTotes();
		while (!totes.isEmpty()) {
			// make sure we don't release too many totes at once
			if (activeTotes < maxTotes) { 
				// send 32R short
				String currentTote = totes.pop().replace("12N", "32R");
				client.send32R(currentTote);
				// start tote on track
				toteController.releaseTote(currentTote, this, client);
				try {
					Thread.sleep(config.getToteReleaseInterval());
				} catch (InterruptedException ie) {
					System.out.println("This shouldn't happen");
				}
			} else {
				System.out.println("Max number of totes on track - waiting....");
			}
			
		}
		
		// All 32R Shorts have gone
		System.out.println("Track controller ended");
	}
	
	private synchronized void incrementActiveTotes () {
		activeTotes++;
	}
	
	private synchronized void decrementActiveTotes () {
		activeTotes--;
	}

	@Override
	public void handleToteActivation(String tote) {
		incrementActiveTotes();
		
	}

	@Override
	public void handleToteDeactivation(String tote) {
		// TODO Auto-generated method stub
		decrementActiveTotes();
	}
}
