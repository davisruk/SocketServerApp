package uk.co.boots.dsp.comms;

import uk.co.boots.dsp.wcs.exceptions.DSPMessageException;

public interface DSPCommunicationHandler {
	public byte[] handleCommsForMessage (DSPCommsMessage message) throws DSPMessageException;
	public String getTypeExtension();
}
