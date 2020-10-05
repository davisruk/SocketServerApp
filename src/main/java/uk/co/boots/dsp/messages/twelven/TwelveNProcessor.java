package uk.co.boots.dsp.messages.twelven;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.tcp.SocketServer;
import uk.co.boots.dsp.events.DSPEventNotifier;
import uk.co.boots.dsp.messages.BasicMessage;
import uk.co.boots.dsp.messages.MessageProcessor;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.service.ToteService;

@Component
public class TwelveNProcessor implements MessageProcessor {

    @Autowired
    private ToteService toteService;
	@Autowired
	@Qualifier("dspEventNotifier")	
	private DSPEventNotifier dspEventNotifier;
	
	private final static byte[] fullResponse = (SocketServer.START_FRAME_CHAR + "0001022N00" + SocketServer.END_FRAME_CHAR).getBytes();
    @Override
	public void process(BasicMessage m) {
		Tote t = (Tote) m;
		m.addRawMessage(fullResponse, "22N", new Date());
		toteService.save(t);
		// send 32R short on other channel
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.TOTE_ORDER_PERSISTED, t));
	}

	@Override
	public byte[] getResponse(BasicMessage m) {
		return fullResponse;
	}

	@Override
	public boolean hasResponse() {
		return true;
	}

}
