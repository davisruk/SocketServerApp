package uk.co.boots.dsp.messages.framework.serialization;

import uk.co.boots.dsp.messages.MessageProcessor;
import uk.co.boots.dsp.messages.framework.entity.BasicMessage;

public interface Deserializer {
	public boolean canHandle(String messageType);
	public BasicMessage deserialize (byte[] messagePayload);
	public MessageProcessor getProcessor();
}
