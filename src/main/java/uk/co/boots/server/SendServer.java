package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.co.boots.osr.OSRBuffer;

@Component
@Setter
public class SendServer implements SocketServer {

	@Value("${tcp_send_port}")
	private int port;
	@Value("${num_messages}")
	private int messages;

	@Autowired
	private OSRBuffer osrBuffer;

	private static ServerSocket sc;

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Send Server started and listening on port " + port);
			while (true) {
				new ClientSocketHandler(sc.accept(), messages).start();
			}
		} catch (IOException ioe) {
		}
	}

	@RequiredArgsConstructor
	private class ClientSocketHandler extends Thread {
		private final Socket client;
		private final int numMessages;
		private DataOutputStream out;
		private DataInputStream din;
		private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

		public void run() {
			try {
				out = new DataOutputStream(client.getOutputStream());
				din = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}

			while (!osrBuffer.isStarted())
				;

			System.out.println("osrBuffer Started");
			// write 32R messages in loop
			osrBuffer.getTotes().forEach(tote -> {
				try {
					out.write(("\n" + tote.replace("12N", "32R") + "\r").getBytes());
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			});

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
			sc.close();
		}
	}

}
