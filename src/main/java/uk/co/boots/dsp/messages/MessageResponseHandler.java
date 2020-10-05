package uk.co.boots.dsp.messages;

import java.io.DataInputStream;

public interface MessageResponseHandler {

	void setInput(DataInputStream din);

	void processResponse();

}
