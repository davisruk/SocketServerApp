package uk.co.boots.dsp.wcs.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.osr.OSRBuffer;
import uk.co.boots.dsp.wcs.service.ToteService;

@Component
public class OrderPersistedHandler extends DSPEventHandlerAdapter {
	
	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	@Qualifier("dspCommunicationNotifier")
	private DSPCommunicationNotifier dspCommunicationNotifier;
	@Autowired
	private ToteService toteService;
	
	
	public OrderPersistedHandler() {
		super("OrderPersistedHandler");
	}
	
	public void handleEvent(ToteEvent event) {
		if (! osrBuffer.sendThirtyTwoRShort()) return;
		if (event.getEventType() == ToteEvent.EventType.TOTE_ORDER_PERSISTED) {
			Tote t = event.getTote();
			DSPCommsMessage msg = toteService.processClientOrderPersisted(t);
			dspCommunicationNotifier.notifyCommunicationHandlers(msg);
			toteService.save(t);
		}
	}
}