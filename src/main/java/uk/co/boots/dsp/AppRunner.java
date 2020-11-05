package uk.co.boots.dsp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.tcp.ReceiveServer;
import uk.co.boots.dsp.comms.tcp.SendServer;
import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.events.DSPEventNotifier;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.track.TrackController;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    SendServer sendServer;
    @Autowired
    ReceiveServer receiveServer;
	@Autowired
	private TrackController trackController;
    
	@Override
    public void run(String... args) throws Exception {
		trackController.start();
		receiveServer.startServer();
		sendServer.startServer();
    }
}