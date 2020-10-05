package uk.co.boots.dsp.comms.tcp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationHandler;
import uk.co.boots.dsp.messages.MessageResponseHandler;

public class SendClientSocketHandler implements DSPCommunicationHandler {
	private final Socket client;
	private DataOutputStream out;
	private DataInputStream din;

	public SendClientSocketHandler(Socket client) {
		this.client = client;
		try {
			out = new DataOutputStream(client.getOutputStream());
			din = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	@Override
	public byte[] handleCommsForMessage(DSPCommsMessage message) {
		return handleCommsForMessage (message.getRawMessage().getMessage().getBytes(), message.getResponsehandler());
	}

	private synchronized byte[] handleCommsForMessage (byte[] message, MessageResponseHandler responseHandler) {
		byte[] ret = (SocketServer.START_FRAME_CHAR + new String(message) + SocketServer.END_FRAME_CHAR).getBytes();
		try {
			responseHandler.setInput(din);
			out.write(ret);
			responseHandler.processResponse();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return ret;
	}
	
	public void close() throws IOException {
		din.close();
		out.close();
		client.close();
		client.close();
	}

	@Override
	public String getTypeExtension() {
		return "TCP";
	}
}
