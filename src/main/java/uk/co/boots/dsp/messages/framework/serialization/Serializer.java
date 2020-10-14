package uk.co.boots.dsp.messages.framework.serialization;

import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.framework.entity.BasicMessage;

public interface Serializer {
	public boolean canHandle(String messageType);
	public byte[] serialize (BasicMessage msg);
	public String getType();
	public MessageResponseHandler getResponseProcessor(BasicMessage message);
}
