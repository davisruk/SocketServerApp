package uk.co.boots.messages;

public interface BasicMessage {
	public boolean hasResponse();
	public byte[] getResponse();
	public void addRawMessage(byte[] rawMessage, String msgType);
}
