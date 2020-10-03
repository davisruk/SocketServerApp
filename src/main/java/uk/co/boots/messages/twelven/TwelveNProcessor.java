package uk.co.boots.messages.twelven;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.persistence.ToteService;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.osr.DSPEventNotifier;
import uk.co.boots.osr.ToteEvent;
import uk.co.boots.server.SocketServer;

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
