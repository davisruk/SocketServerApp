package uk.co.boots.messages;

import java.util.Date;

public interface BasicMessage {
	public boolean hasResponse();
	public byte[] getResponse();
	public void addRawMessage(byte[] rawMessage, String msgType, Date creationTime);
}
