package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

	@Autowired
	private DeserializerFactory deserializerFactory;

	private static ServerSocket sc;

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Receive Server started and listening on port " + port);
			while (true) {
				new ClientSocketHandler(sc.accept()).start();
			}
		} catch (IOException ioe) {
		}
	}
	

	@RequiredArgsConstructor
	private class ClientSocketHandler extends Thread {
		private final Socket client;
		@Setter
		boolean finished = false;

		public void run() {
			try {
				InputStream ins = new BufferedInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				while (!finished) {
					byte[] messageBytes;
					boolean finishedMessage = false;
						ByteArrayOutputStream buf = new ByteArrayOutputStream();
						int b;
						while (!finishedMessage && (b = ins.read()) > 0) {
							buf.write(b);
							finishedMessage = b == 0x0A;
						}
					messageBytes = buf.toByteArray();
					String msgType = new String(messageBytes, 1, 3);
					Deserializer d = deserializerFactory.getDeserializer(msgType).get();
					BasicMessage m = d.deserialize(messageBytes);
					MessageProcessor mp = d.getProcessor();
					mp.process(m);
					if (mp.hasResponse()) {
						out.write(mp.getResponse(m));
					}				
				}
				ins.close();
				out.close();
				client.close();
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}

		public void finish() {
			setFinished(true);
		}
	}

}
