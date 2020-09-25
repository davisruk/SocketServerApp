package uk.co.boots.osr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.SerializerFactory;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.Status;
import uk.co.boots.messages.thirtytwor.StatusArrayList;
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
		StartTime st = new StartTime();
		st.setPayload(convertTime(started));
		tote.setStartTime(st);
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
		EndTime et = new EndTime();
		et.setPayload(convertTime(System.currentTimeMillis()));
		tote.setEndTime(et);
		setupToteStatus(tote);
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		// need to get a tote processor to setup data that would be created whilst on the track
		
		client.sendMessage(s.serialize(tote));
		handler.handleToteDeactivation(tote);
	}
	
	private String convertTime(long timeInMillis) {
		long millis = timeInMillis / 1000;
		long second = millis % 60;
		long minute = (timeInMillis / second) % 60;
		long hour = (timeInMillis / minute) % 24;
		return String.format("%02d%02d%02d", hour, minute, second);
	}
	
	private void setupToteStatus (Tote t) {
		StatusArrayList sal = new StatusArrayList();
		sal.setNumberOfLines(1);
		sal.add(new Status("0030"));
		t.setStatus(sal);
	}

}
