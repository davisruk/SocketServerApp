package uk.co.boots.dsp.comms.tcp;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Setter;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.wcs.TrackController;

@Component
@Setter
public class SendServer implements SocketServer {

	@Value("${tcp_send_port}")
	private int port;

	@Autowired
	@Qualifier("dspCommunicationNotifier")
	private DSPCommunicationNotifier dspCommunicationNotifier;
	
	private static ServerSocket sc;

	private SendClientSocketHandler currentClient; 
	
	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Send Server started and listening on port " + port);
			while (true) {
				SendClientSocketHandler client = new SendClientSocketHandler(sc.accept());
				if (currentClient != null) {
					// we only expect 1 tcp client so replace the current one
					dspCommunicationNotifier.replaceDSPCommunicationHandler(currentClient, client);
					// we may have had some failed messages so ask the comms notifier to resend them
					// with the new handler
					dspCommunicationNotifier.sendFailedMessages(client);
				} else {
					dspCommunicationNotifier.registerDSPCommunicationHandler(client);
				}
				currentClient = client;
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
