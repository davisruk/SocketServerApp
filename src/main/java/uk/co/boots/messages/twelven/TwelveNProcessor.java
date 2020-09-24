package uk.co.boots.messages.twelven;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.MessageProcessor;

public class TwelveNProcessor implements MessageProcessor {

	@Override
	public void process(BasicMessage m) {
		// TODO Auto-generated method stub
		TwelveN twelveN = (TwelveN) m;
	}

	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return "22N".getBytes();
	}

	@Override
	public boolean hasResponse() {
		// TODO Auto-generated method stub
		return true;
	}

}
