package uk.co.boots.messages.twelven;

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
	
	@Override
	public void process(BasicMessage m) {
		Tote t = (Tote) m;
		toteService.save(t);
	}

	@Override
	public byte[] getResponse(BasicMessage m) {
		// TODO Get the 22N Serializer and return message
		return "22N".getBytes();
	}

	@Override
	public boolean hasResponse() {
		return true;
	}

}
