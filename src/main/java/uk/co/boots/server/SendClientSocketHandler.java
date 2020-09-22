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
	private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public SendClientSocketHandler(Socket client) {
		this.client = client;
		try {
			out = new DataOutputStream(client.getOutputStream());
			din = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	private synchronized void sendMessage (String message) {
		try {
			out.write(("\n" + message + "\r").getBytes());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	public void send32R (String message) {
		sendMessage("[32R Short] " + message);
	}
	
	public void send32RLong (String message) {
		sendMessage ("[32R Long] " + message);
	}
	
	private byte[] addBytesToArray(byte[] toArray, byte[] fromArray, int numBytesToAdd) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (toArray != null && toArray.length > 0)
			outputStream.write(toArray);
		if (fromArray != null && fromArray.length > 0)
			outputStream.write(fromArray, 0, numBytesToAdd);
		return outputStream.toByteArray();
	}

	private String bytesToHex(byte[] bytes, int bytesToConvert) {
		char[] hexChars = new char[bytesToConvert * 5];
		for (int j = 0; j < bytesToConvert; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 5] = '0';
			hexChars[j * 5 + 1] = 'x';
			hexChars[j * 5 + 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 5 + 3] = HEX_ARRAY[v & 0x0F];
			hexChars[j * 5 + 4] = ',';
		}
		return new String(hexChars);
	}

	public void close() throws IOException {
		din.close();
		out.close();
		client.close();
		client.close();
	}
}
