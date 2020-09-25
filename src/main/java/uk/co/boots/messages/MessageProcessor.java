package uk.co.boots.messages;

public interface MessageProcessor {
	public void process(BasicMessage m);
	public byte[] getResponse (BasicMessage m);
	public boolean hasResponse();
}