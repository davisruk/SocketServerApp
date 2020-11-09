package uk.co.boots.dsp.comms.tcp;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Setter;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.wcs.events.DSPEventNotifier;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.events.handlers.EventLogger;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
@Setter
public class SendServer implements SocketServer, ChannelChangeNotifier {

	@Value("${tcp_send_port}")
	private int port;
	@Autowired
	@Qualifier("dspCommunicationNotifier")
	private DSPCommunicationNotifier dspCommunicationNotifier;
	private ServerSocket sc;
	private SendClientSocketHandler currentClient;
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	@Autowired
	@Qualifier("dspEventNotifier")	
	private DSPEventNotifier dspEventNotifier;
	@Autowired
	TrackStatus trackStatus;
	
	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			logger.info("[SendServer::startServer] TCP Socket Server started and listening on port " + port);
			while (true) {
				SendClientSocketHandler client = new SendClientSocketHandler(sc.accept(), this);
				if (currentClient != null) {
					notifyChannelChange(client.getClient().getInetAddress().getHostAddress() + ":" + client.getClient().getPort());

					// we only expect 1 tcp client so replace the current one
					dspCommunicationNotifier.replaceDSPCommunicationHandler(currentClient, client);
					// we may have had some failed messages so ask the comms notifier to resend them
					// with the new handler
					dspCommunicationNotifier.sendFailedMessages(client);
				} else {
					notifyChannelChange(client.getClient().getInetAddress().getHostAddress() + ":" + client.getClient().getPort());
					dspCommunicationNotifier.registerDSPCommunicationHandler(client);
				}
				currentClient = client;
			}
		} catch (IOException ioe) {
			notifyChannelChange("Disconnected");
			logger.error("[SendServer::startServer] " + ioe.getMessage());
		}
	}
	
	public void notifyChannelChange(String change) {
		trackStatus.setSendChannelClient(change);
		dspEventNotifier.notifyEventHandlers(new ToteEvent(ToteEvent.EventType.SEND_CHANNEL_CHANGE, null));
		
	}
}
