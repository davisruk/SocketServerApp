package uk.co.boots.dsp.comms.tcp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationHandler;
import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.wcs.events.handlers.EventLogger;
import uk.co.boots.dsp.wcs.exceptions.DSPMessageException;

public class SendClientSocketHandler implements DSPCommunicationHandler {
	@Getter
	private final Socket client;
	private DataOutputStream out;
	private DataInputStream din;
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	private ChannelChangeNotifier channelChangeNotifier;
	
	public SendClientSocketHandler(Socket client, ChannelChangeNotifier channelChangeNotifier) {
		this.client = client;
		this.channelChangeNotifier = channelChangeNotifier;
		try {
			out = new DataOutputStream(client.getOutputStream());
			din = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		} catch (IOException ioe) {
			channelChangeNotifier.notifyChannelChange("Disconnected");
			logger.error("[SendClientSocketHandler::SendClientSocketHandler] " + ioe.getMessage());
		}
	}

	@Override
	public byte[] handleCommsForMessage(DSPCommsMessage message) throws DSPMessageException {
		return handleCommsForMessage(message.getRawMessage().getMessage().getBytes(), message.getResponsehandler());
	}

	private synchronized byte[] handleCommsForMessage(byte[] message, MessageResponseHandler responseHandler)
			throws DSPMessageException {
		byte[] ret = (SocketServer.START_FRAME_CHAR + new String(message) + SocketServer.END_FRAME_CHAR).getBytes();
		try {
			out.write(ret);
		} catch (IOException ioe) {
			channelChangeNotifier.notifyChannelChange("Disconnected");
			throw new DSPMessageException("Error writing message");
		}

		responseHandler.setInput(din);
		try {
			responseHandler.processResponse();
		} catch (Exception dspme) {
			try {
				// something went wrong try and close the socket
				channelChangeNotifier.notifyChannelChange("Disconnected");
				close();
			} catch (IOException e) {
				// do nothing we're going to make the caller handle it
			}
			// throw exception to caller
			throw dspme;
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
