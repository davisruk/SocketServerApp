package uk.co.boots.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Setter;
import uk.co.boots.osr.TrackController;

@Component
@Setter
public class SendServer implements SocketServer {

	@Value("${tcp_send_port}")
	private int port;

	@Autowired
	private TrackController trackController;

	private static ServerSocket sc;

	@Async
	public void startServer() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Send Server started and listening on port " + port);
			while (true) {
				trackController.handleClientSocketConnection(new SendClientSocketHandler(sc.accept()));
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
