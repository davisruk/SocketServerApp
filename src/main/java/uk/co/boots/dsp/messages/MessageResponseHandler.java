package uk.co.boots.dsp.messages;

import java.io.DataInputStream;

import uk.co.boots.dsp.wcs.exceptions.DSPMessageException;

public interface MessageResponseHandler {

	void setInput(DataInputStream din);

	void processResponse() throws DSPMessageException;

}
