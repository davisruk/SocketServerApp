package uk.co.boots.dsp.messages;

import uk.co.boots.dsp.messages.framework.entity.BasicMessage;

public interface MessageProcessor {
	public void process(BasicMessage m);
	public byte[] getResponse (BasicMessage m);
	public boolean hasResponse();
}
