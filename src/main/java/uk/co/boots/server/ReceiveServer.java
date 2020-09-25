package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.DeserializerFactory;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.osr.OSRBuffer;

@Component
@Setter
public class ReceiveServer implements SocketServer {

	@Value("${tcp_receive_port}")
	private int port;
	@Value("${num_messages}")
	private int messages;

	@Autowired
	private OSRBuffer osrBuffer;
	@Autowired
	private DeserializerFactory deserializerFactory;

	private static ServerSocket sc;

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Receive Server started and listening on port " + port);
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
				byte[] readBuffer = new byte[10000];
				int totalRead = 0;
				int bytesRead = 0;
				byte[] finalBuffer = null;

				out = new DataOutputStream(client.getOutputStream());
				din = new DataInputStream(new BufferedInputStream(client.getInputStream()));

				while ((bytesRead = din.read(readBuffer)) > 0) {
					// append read bytes to buffer
					finalBuffer = addBytesToArray(finalBuffer, readBuffer, bytesRead);
					totalRead += bytesRead;
					// check if we still have bytes available on the stream - continue to build the
					// buffer if not
					if (din.available() == 0) {
						// we've exhausted the internal stream buffer so process the data
						String s = bytesToHex(finalBuffer, totalRead);
						String readString = new String(finalBuffer, 0, totalRead, StandardCharsets.UTF_8);
						String outputString = "Server received string: " + readString + "HEX:["
								+ s.substring(0, s.length() - 1) + "]";
						Deserializer d = deserializerFactory.getDeserializer(readString.substring(1, 4)).get();
						BasicMessage m = d.deserialize(finalBuffer);
						MessageProcessor mp = d.getProcessor();
						mp.process(m);
						if (mp.hasResponse()) {
							out.write(mp.getResponse(m));
						}

						// reset vars for next message on internal stream buffer
						totalRead = 0;
						finalBuffer = null;
					}
				}
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
			sc.close();
		}
	}

}
