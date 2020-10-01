package uk.co.boots.messages.twelven;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.persistence.ToteService;
import uk.co.boots.messages.shared.Tote;

@Component
public class TwelveNProcessor implements MessageProcessor {

    @Autowired
    private ToteService toteService;
	
	private final static byte[] fullResponse = ("\n" + "0001022N00" + "\r").getBytes();
    @Override
	public void process(BasicMessage m) {
		Tote t = (Tote) m;
		m.addRawMessage(fullResponse, "22N", new Date());
		toteService.save(t);
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
