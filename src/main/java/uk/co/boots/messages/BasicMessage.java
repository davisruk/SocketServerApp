package uk.co.boots.messages;

public interface BasicMessage {
	public boolean hasResponse();
	public byte[] getResponse();
}
