package uk.co.boots.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.Deserializer;
import uk.co.boots.messages.DeserializerFactory;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.persistence.ToteService;

@Component
public class ReceiveServer implements SocketServer {

	@Value("${tcp_receive_port}")
	private int port;

	@Value("${message_type_offset}")
	private int messageTypePos;
	
	@Value("${message_type_length}")
	private int messageTypeLength;

	@Autowired
	private DeserializerFactory deserializerFactory;

	@Autowired
	ToteService toteService;
	
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
			System.out.println("[Message Receiver] Handling client messages");
			while (!finished) {
				byte[] messageBytes;
				boolean finishedMessage = false;
				boolean messageStarted = false;
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int b, bytesRead = 0;
				while (!finishedMessage && (b = ins.read()) > 0) {
					bytesRead++;
					if (bytesRead == 1) {
						// keep checking until we hit frame start 0x0A
						// some tools will replace 0x0A with a space
						messageStarted = b == START_FRAME || b == ALTERNATIVE_START_FRAME;
					}
					if (messageStarted) {
						//check for end frame 0x0D
						buf.write(b);
						finishedMessage = b == END_FRAME;
					} else {
						bytesRead = 0;
					}
				}
				messageBytes = buf.toByteArray();
				String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
				Deserializer d = deserializerFactory.getDeserializer(msgType).get();
				BasicMessage m = d.deserialize(messageBytes);
				m.addRawMessage(messageBytes, msgType, new Date());
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
