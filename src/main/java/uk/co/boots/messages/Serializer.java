package uk.co.boots.messages;

import uk.co.boots.server.MessageResponseHandler;

public interface Serializer {
	public boolean canHandle(String messageType);
	public byte[] serialize (BasicMessage msg);
	public String getType();
	public MessageResponseHandler getResponseProcessor(BasicMessage message);
}
