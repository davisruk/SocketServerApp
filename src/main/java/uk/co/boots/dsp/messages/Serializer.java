package uk.co.boots.dsp.messages;

public interface Serializer {
	public boolean canHandle(String messageType);
	public byte[] serialize (BasicMessage msg);
	public String getType();
	public MessageResponseHandler getResponseProcessor(BasicMessage message);
}
