package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.DeserializerFactory;
import uk.co.boots.messages.MessageProcessor;

@Component
public class ReceiveServer implements SocketServer {

	@Value("${tcp_receive_port}")
	private int port;

	@Autowired
	private DeserializerFactory deserializerFactory;

	private static ServerSocket sc;
	private boolean finished = false;
	
	public synchronized void setFinished (boolean val) {
		finished = val;
	}

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Receive Server started and listening on port " + port);
			while (true) {
				handleClientSocketConnection(sc.accept());
			}
		} catch (IOException ioe) {
		}
	}

	@Async
	private void handleClientSocketConnection(Socket client) {
		try {
			InputStream ins = new BufferedInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			while (!finished) {
				byte[] messageBytes;
				boolean finishedMessage = false;
				boolean messageStarted = false;
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int b, bytesRead = 0;
				while (!finishedMessage && (b = ins.read()) > 0) {
					bytesRead++;
					if (bytesRead == 1) {
						// this is the first byte, we want to ensure that it's a 0x0D framing byte
						// some tools will replace 0x0D with a space
						messageStarted = b == 0x0D || b == 0x20;
					}
					if (messageStarted) {
						buf.write(b);
						finishedMessage = b == 0x0A;
					} else {
						bytesRead = 0;
					}
				}
				messageBytes = buf.toByteArray();
				String msgType = new String(messageBytes, 6, 3);
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
}
