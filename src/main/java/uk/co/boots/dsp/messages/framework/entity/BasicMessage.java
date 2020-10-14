package uk.co.boots.dsp.messages.framework.entity;

import java.util.Date;

public interface BasicMessage {
	public boolean hasResponse();
	public byte[] getResponse();
	public void addRawMessage(byte[] rawMessage, String msgType, Date creationTime);
}
