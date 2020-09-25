package uk.co.boots.messages;

public interface Serializer {
	public boolean canHandle(String messageType);
	public byte[] serialize (BasicMessage msg);
}
