/* Class for sending status information back to a websocket client on subscription */

package uk.co.boots.dsp.comms.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscriptionListener implements ApplicationListener<SessionSubscribeEvent>{
	@Autowired
	WebSocketController webSocketController;
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		webSocketController.send();
	}

}
