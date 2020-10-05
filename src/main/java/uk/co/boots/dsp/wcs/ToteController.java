package uk.co.boots.dsp.wcs;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.events.DSPEventNotifier;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.service.ToteService;

@Component
public class ToteController {
	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private ToteService toteService;
	@Autowired
    @Qualifier("dspEventNotifier")	
	private DSPEventNotifier dspEventNotifier;

	
	@Async
	public void releaseTote(Tote tote) {
		if (tote == null) return;
		long started = System.currentTimeMillis();
		long trackTravelTimeLeft = osrBuffer.getToteTravelTime();
		long timeTravelled = System.currentTimeMillis() - started;

		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_ACTIVATED, tote));
		toteService.setupStartTime(Calendar.getInstance(), tote);
		String toteName = tote.getHeader().getOrderId() + "_" + tote.getHeader().getSheetNumber();
		System.out.println(toteName + " started Travelling around track");
		
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

		System.out.println(toteName + " finished Travelling around track in " + timeTravelled / 1000 + " seconds");

		toteService.setupEndTime(Calendar.getInstance(), tote);
		toteService.setupOperators(tote);
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_RELEASED_FOR_DELIVERY, tote));
		// signal tote has ended
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_DEACTIVATED, tote));
	}
}
