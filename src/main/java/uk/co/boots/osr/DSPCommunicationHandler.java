package uk.co.boots.osr;

public interface DSPCommunicationHandler {
	public byte[] handleCommsForMessage (DSPCommsMessage message);
	public String getTypeExtension();
}
