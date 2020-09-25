package uk.co.boots.osr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.SerializerFactory;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class ToteController {

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private SerializerFactory serializerFactory; 
	
	@Async
	public void releaseTote (Tote tote, ToteEventHandler handler, SendClientSocketHandler client) {
		handler.handleToteActivation(tote);
		long started = System.currentTimeMillis();
		long trackTravelTimeLeft = osrBuffer.getOsrConfig().getToteTrackTravelTime();
		long timeTravelled = System.currentTimeMillis() - started; 
		while (timeTravelled <= trackTravelTimeLeft) {
			try {
				System.out.println(tote + " started Travelling around track");
				Thread.sleep(trackTravelTimeLeft);
				timeTravelled = System.currentTimeMillis() - started;
				System.out.println(tote + " finished Travelling around track in " + timeTravelled / 1000 + "seconds");

			} catch (InterruptedException ie) {
				System.out.println("Tote Travel Interrupted");
				timeTravelled = System.currentTimeMillis() - started;
				trackTravelTimeLeft -= timeTravelled;
				timeTravelled = 0L;
				started = System.currentTimeMillis();
			}
		}
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		client.sendMessage(s.serialize(tote));
		handler.handleToteDeactivation(tote);
	}

}
