package uk.co.boots.dsp.comms.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import uk.co.boots.dsp.wcs.track.TrackStatus;

@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {
	
	private SimpMessagingTemplate template;
	@Autowired
	TrackStatus trackStatus;
	
	@Autowired
	public WebSocketController (SimpMessagingTemplate template){
		this.template = template;
	}
	
	public void send() {
		// TODO Auto-generated method stub
		try {
			this.template.convertAndSend("/topic/livestats", trackStatus);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}

	@MessageMapping("/trackStatus")
	public void trackStatus() {
		send();
	}
}
