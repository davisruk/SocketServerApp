package uk.co.boots.server;

import java.io.DataInputStream;

public interface MessageResponseHandler {

	void setInput(DataInputStream din);

	void processResponse();

}
