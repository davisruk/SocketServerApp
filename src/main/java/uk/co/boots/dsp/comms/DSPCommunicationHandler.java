package uk.co.boots.dsp.comms;

public interface DSPCommunicationHandler {
	public byte[] handleCommsForMessage (DSPCommsMessage message);
	public String getTypeExtension();
}
