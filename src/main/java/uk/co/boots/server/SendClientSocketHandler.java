package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.RequiredArgsConstructor;

public class SendClientSocketHandler {
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
	
	public synchronized byte[] sendMessage (byte[] message, MessageResponseHandler responseHandler) {
		byte[] ret = ("\n" + new String(message) + "\r").getBytes();
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
}
