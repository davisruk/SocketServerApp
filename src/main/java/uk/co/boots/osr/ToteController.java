package uk.co.boots.osr;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.persistence.ToteService;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class ToteController {
	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private ToteService toteService;
	
	@Async
	public void releaseTote(Tote tote, ToteEventHandler handler, SendClientSocketHandler client) {
		long started = System.currentTimeMillis();
		long trackTravelTimeLeft = osrBuffer.getOsrConfig().getToteTrackTravelTime();
		long timeTravelled = System.currentTimeMillis() - started;

		handler.handleToteActivation(tote);
		toteService.setupStartTime(Calendar.getInstance(), tote);
		System.out.println(tote.getToteIdentifier().getPayload() + " started Travelling around track");
		
		while (timeTravelled <= trackTravelTimeLeft) {
			try {
				Thread.sleep(trackTravelTimeLeft);
				timeTravelled = System.currentTimeMillis() - started;
			} catch (InterruptedException ie) {
				System.out.println("Tote Travel Interrupted");
				timeTravelled = System.currentTimeMillis() - started;
				trackTravelTimeLeft -= timeTravelled;
				timeTravelled = 0L;
				started = System.currentTimeMillis();
			}
		}

		System.out.println(tote.getToteIdentifier().getPayload() + " finished Travelling around track in " + timeTravelled / 1000 + " seconds");

		toteService.setupEndTime(Calendar.getInstance(), tote);
		toteService.setupToteStatus(tote, "0030");
		toteService.setupOperators(tote);
		toteService.handleToteFinished(tote, client);
		// signal tote has ended
		handler.handleToteDeactivation(tote);
	}
}
