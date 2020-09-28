package uk.co.boots.osr;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.persistence.ToteRepository;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class TrackController implements ToteEventHandler {

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired 
	private ToteController toteController;
	@Autowired
	private ToteRepository toteRepository;
	
	@Getter
	private int activeTotes;

	private SendClientSocketHandler client;

	@Async
	public void start(SendClientSocketHandler client) {
		this.client = client;
		System.out.println("Track controller started");
		OSRConfig config = osrBuffer.getOsrConfig(); 
		int maxTotes = config.getMaxTotesOnTrack();
		System.out.println("osrBuffer Started");
		// write 32R messages in loop
		ConcurrentLinkedDeque<Tote> totes = osrBuffer.getTotes();
		// osrBuffer needs to be releasing totes - wait if not 
		while (!config.isReleasing())
			;			

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("id")));
		Page<Tote> page = toteRepository.findAll(pageable);
		int pageIndex = 0;
		while (page.getNumberOfElements() > 0) {
			System.out.println(page.getNumberOfElements());
			page.forEach(t -> {
				while (!config.isReleasing())
					;			
				// make sure we don't release too many totes at once
				if (activeTotes < maxTotes) { 
					// send 32R short
					// start tote on track
					toteController.releaseTote(t, this, client);
					try {
						Thread.sleep(config.getToteReleaseInterval());
					} catch (InterruptedException ie) {
						System.out.println("This shouldn't happen");
					}
				} else {
					System.out.println("Max number of totes on track - waiting....");
				}
			});
			pageable = PageRequest.of(++pageIndex, 10, Sort.by(Order.asc("id")));;
			page = toteRepository.findAll(pageable);
		}
		// All 32R Shorts have gone
		System.out.println("Track controller ended");
	}

/*		
		while (!totes.isEmpty()) {
			// osrBuffer needs to be releasing totes - wait if not 
			while (!config.isReleasing())
				;			
			// make sure we don't release too many totes at once
			if (activeTotes < maxTotes) { 
				// send 32R short
				Tote currentTote = totes.pop();
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
		
	}
*/	
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
		// TODO Auto-generated method stub
		decrementActiveTotes();
	}
}
