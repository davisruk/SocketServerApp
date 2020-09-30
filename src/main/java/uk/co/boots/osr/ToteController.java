package uk.co.boots.osr;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.SerializerFactory;
import uk.co.boots.messages.persistence.ToteService;
import uk.co.boots.messages.shared.OrderDetail;
import uk.co.boots.messages.shared.OrderLine;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.OperatorDetail;
import uk.co.boots.messages.thirtytwor.OperatorLine;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.Status;
import uk.co.boots.messages.thirtytwor.ToteStatusDetail;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class ToteController {
	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private SerializerFactory serializerFactory;
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
		toteService.save(tote);
		
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		System.out.println("Sending message back");
		client.sendMessage(s.serialize(tote));
		System.out.println("Finished Sending message back");
		// signal tote has ended
		handler.handleToteDeactivation(tote);
	}
}
