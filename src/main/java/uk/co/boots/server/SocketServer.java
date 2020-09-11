package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class SocketServer {
	@Getter
	@Value("${tcp_port}")
	private int port;

	private ServerSocket sc;

	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Server started and listening on port " + port);
			while (true) {
				new ClientSocketHandler(sc.accept()).start();
			}
		} catch (IOException ioe) {
		}
	}

	@RequiredArgsConstructor
	private static class ClientSocketHandler extends Thread {
		private final Socket client;
		private DataOutputStream out;
		private DataInputStream din;
		private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

		public void run() {
			try {
				byte[] readBuffer = new byte[10];
				int totalRead = 0;
				int bytesRead = 0;
				byte[] finalBuffer = null;

				out = new DataOutputStream(client.getOutputStream());
				din = new DataInputStream(new BufferedInputStream(client.getInputStream()));
				// write 32R messages in loop
				for (int i = 0; i < 5; i++) {
					out.write("\r0005232R1403TOTE0000167865001T0205s06175941O01040001\n".getBytes());
					// block until we receive a 42R
					boolean stop = false;
					while (!stop && (bytesRead = din.read(readBuffer)) > 0) {
						// append read bytes to buffer
						finalBuffer = addBytesToArray(finalBuffer, readBuffer, bytesRead);
						totalRead += bytesRead;
						// check if we still have bytes available on the stream - continue to build the
						// buffer if not
						if (din.available() == 0) {
							// we've exhausted the internal stream buffer so process the data
							String s = bytesToHex(finalBuffer, totalRead);
							String readString = new String(finalBuffer, 0, totalRead, StandardCharsets.UTF_8);
							String outputString = "Server received string: " + readString + "\nHEX:["
									+ s.substring(0, s.length() - 1) + "]\n";
							// out.write(outputString.getBytes());

							System.out.println(outputString);
							// reset vars for next message on internal stream buffer
							totalRead = 0;
							finalBuffer = null;
							stop = true;
						}
					}
				}
				close();
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
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
		}
	}
}
