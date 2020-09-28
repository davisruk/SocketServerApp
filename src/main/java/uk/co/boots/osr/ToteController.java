package uk.co.boots.osr;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.SerializerFactory;
import uk.co.boots.messages.shared.OrderLineArrayList;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.OperatorArrayList;
import uk.co.boots.messages.thirtytwor.OperatorLine;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.Status;
import uk.co.boots.messages.thirtytwor.StatusArrayList;
import uk.co.boots.server.SendClientSocketHandler;

@Component
public class ToteController {
	// this class breaks encapsulation in several places
	// it should not know the format of strings
	// the tote class should really store native types where possible
	// and the serializer should convert them to strings when required
	// areas that should be refactored are marked
	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private SerializerFactory serializerFactory;

	@Async
	public void releaseTote(Tote tote, ToteEventHandler handler, SendClientSocketHandler client) {
		handler.handleToteActivation(tote);
		long started = System.currentTimeMillis();
		StartTime st = new StartTime();
		Calendar startCal = Calendar.getInstance();
		// poor encapsulation - refactor
		st.setPayload(convertTime(startCal, "%02d%02d%02d"));
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

		Calendar endCal = Calendar.getInstance();
		EndTime et = new EndTime();
		// poor encapsulation - refactor
		et.setPayload(convertTime(endCal, "%02d%02d%02d"));
		tote.setEndTime(et);
		setupToteStatus(tote);
		setupOperators(tote, startCal, endCal);
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		// need to get a tote processor to setup data that would be created whilst on
		// the track
		System.out.println("Sending message back");
		client.sendMessage(s.serialize(tote));
		System.out.println("Finished Sending message back");
		handler.handleToteDeactivation(tote);
	}

	private void setupOperators(Tote t, Calendar start, Calendar end) {
		OperatorArrayList oal = new OperatorArrayList();
		oal.setNumberOfLines(1);
		OperatorLine op = new OperatorLine();
		op.setOperatorId("RDavis  ");
		op.setRoleId("Solution Architect  ");
		oal.add(op);
		
		OrderLineArrayList olal = t.getOrderLines(); 
		if (olal != null) { 
			olal.forEach(line -> {
				Calendar opc = Calendar.getInstance();
				opc.setTimeInMillis(start.getTimeInMillis() - end.getTimeInMillis() / 2);
				// poor encapsulation - refactor
				op.setTimestamp(convertDate(opc) + " " + convertTime(opc, "%02d.%02d.%02d"));
				line.setOperators(oal);
			});
		}
	}

	private String convertDate(Calendar c) {
		return String.format("%02d.%02d.%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
				c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH));
	}

	private String convertTime(Calendar c, String format) {
		return String.format(format, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	private void setupToteStatus(Tote t) {
		StatusArrayList sal = new StatusArrayList();
		sal.setNumberOfLines(1);
		sal.add(new Status("0030"));
		t.setStatus(sal);
	}

}
