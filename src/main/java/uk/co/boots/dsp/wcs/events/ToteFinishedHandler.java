package uk.co.boots.dsp.wcs.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.service.ToteService;

@Component
public class ToteFinishedHandler extends DSPEventHandlerAdapter {
	@Autowired
	private ToteService toteService;
	@Autowired
	@Qualifier("dspCommunicationNotifier")
	private DSPCommunicationNotifier dspCommunicationNotifier;
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	public ToteFinishedHandler() {
		super("ToteFinishedHandler");
	} 
	@Override
	public void handleEvent(ToteEvent event) {
		if (event.getEventType() == ToteEvent.EventType.TOTE_RELEASED_FOR_DELIVERY) {
			Tote t = event.getTote();
			logger.info("[ToteFinishedHandler::handleEvent] Tote Finished");
			DSPCommsMessage msg = toteService.processToteFinished(t);
			dspCommunicationNotifier.notifyCommunicationHandlers(msg);
			toteService.save(t);
			logger.info("[ToteFinishedHandler::handleEvent] Tote Saved");
		}
	}
}
