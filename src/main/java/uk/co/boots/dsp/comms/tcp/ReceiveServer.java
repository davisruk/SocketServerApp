/*
 * Receives DSP Messages and sends back their responses
 * Much simpler class than for sending back messages on other channel
 * There is limited fault tolerance built in - if the connection
 * fails when writing back a response then when the client
 * reconnects the response will be returned again
 * 
 * If the client drops the connection during message transmission from
 * its side then it is expected to re-send the message
 */
package uk.co.boots.dsp.comms.tcp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.dsp.messages.MessageProcessor;
import uk.co.boots.dsp.messages.framework.entity.BasicMessage;
import uk.co.boots.dsp.messages.framework.serialization.Deserializer;
import uk.co.boots.dsp.messages.framework.serialization.DeserializerFactory;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.service.ToteService;

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
	
	private byte[] currentResponse = null;
	private boolean writingResponse;
	
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	public synchronized void setFinished (boolean val) {
		finished = val;
	}

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			logger.info("[ReceiveServer::startServer] TCP socket server started and listening on port " + port);
			while (true) {
				TCPComms comms = new TCPComms(sc.accept());
				handleOldMessage(comms);
				handleClientSocketConnection(comms);
			}
		} catch (IOException ioe) {
		}
	}

	private synchronized void handleOldMessage (TCPComms comms) {
		if (currentResponse == null || !writingResponse) return;
		try {
			comms.out.write(currentResponse);
			setResponseValues(false, null);
		} catch (IOException ioe) {
			// do nothing - client will reconnect and we will resend
		}
	}
	
	@Async
	private void handleClientSocketConnection(TCPComms comms) {
		try {
			logger.info("[ReceiveServer::handleClientSocketConnection] Handling client messages");
			setFinished(false);
			while (!finished) {
				byte[] messageBytes;
				boolean finishedMessage = false;
				boolean messageStarted = false;
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int b, bytesRead = 0;
				while (!finishedMessage && (b = comms.in.read()) > 0) {
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
				if (finishedMessage) {
					messageBytes = buf.toByteArray();
					String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
					Deserializer d = deserializerFactory.getDeserializer(msgType).get();
					BasicMessage m = d.deserialize(messageBytes);
					m.addRawMessage(messageBytes, msgType, new Date());
					MessageProcessor mp = d.getProcessor();
					mp.process(m);
					if (mp.hasResponse()) {
						setResponseValues(true, mp.getResponse(m));
						comms.out.write(currentResponse);
						setResponseValues(false, null);
					}
				} else {
					logger.info("[ReceiveServer::handleClientSocketConnection] Message not read correctly - close connection");
					finished = true;
				}
			}
		} catch (IOException ioe) {
			logger.error("[ReceiveServer::handleClientSocketConnection] " + ioe.getMessage());
		} finally {
			comms.closeComms();
		}
		
	}
	
	private synchronized void setResponseValues (boolean writing, byte[] response) {
		writingResponse = writing;
		currentResponse = response;
	}
	
	@Data
	private class TCPComms {
		private Socket client;
		private InputStream in;
		private DataOutputStream out;
		
		public TCPComms (Socket client) {
			try {
				this.client = client;
				in = new BufferedInputStream(client.getInputStream());
				out = new DataOutputStream(client.getOutputStream());
			} catch (IOException ioe) {
				// do nothing
			}
		}
		
		public void closeComms() {
			try {
				in.close();
				out.close();
				client.close();
			}catch(IOException ioe) {
				// do nothing
			}
		}
	}
}
