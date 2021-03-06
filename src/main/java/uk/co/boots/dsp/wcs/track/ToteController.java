package uk.co.boots.dsp.wcs.track;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.events.DSPEventNotifier;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.osr.OSRBuffer;
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

	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	@Async
	public void releaseTote(Tote tote) {
		if (tote == null) return;
		long started = System.currentTimeMillis();
		long trackTravelTimeLeft = osrBuffer.getToteTravelTime();
		toteService.setupStartTime(Calendar.getInstance(), tote);
		String toteName = tote.getHeader().getOrderId() + "_" + tote.getHeader().getSheetNumber();
		logger.info("[ToteController::releaseTote] " + toteName + " started Travelling around track");
		toteService.setupTransportContainer(tote);
		toteService.setupOrderLines(tote);
		long timeTravelled = System.currentTimeMillis() - started;
		while (timeTravelled <= trackTravelTimeLeft) {
			try {
				Thread.sleep(trackTravelTimeLeft);
				timeTravelled = System.currentTimeMillis() - started;
			} catch (InterruptedException ie) {
				logger.info("[ToteController::releaseTote] Tote Travel Interrupted");
				timeTravelled = System.currentTimeMillis() - started;
				trackTravelTimeLeft -= timeTravelled;
				timeTravelled = 0L;
				started = System.currentTimeMillis();
			}
		}
		logger.info("[ToteController::releaseTote] " + toteName + " finished Travelling around track in " + timeTravelled / 1000 + " seconds");
		toteService.setupEndTime(Calendar.getInstance(), tote);		
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_RELEASED_FOR_DELIVERY, tote));
		// signal tote has ended
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_DEACTIVATED, tote));
	}
}
