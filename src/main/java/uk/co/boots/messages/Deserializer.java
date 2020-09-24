package uk.co.boots.messages;

public interface Deserializer {
	public BasicMessage deserialize (byte[] messagePayload);
	public MessageProcessor getProcessor();
}
