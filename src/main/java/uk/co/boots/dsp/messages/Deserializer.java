package uk.co.boots.dsp.messages;

public interface Deserializer {
	public boolean canHandle(String messageType);
	public BasicMessage deserialize (byte[] messagePayload);
	public MessageProcessor getProcessor();
}
