package uk.co.boots.dsp.comms.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import uk.co.boots.dsp.wcs.events.DSPEventHandler;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Controller
public class WebSocketController implements DSPEventHandler {
	
	private SimpMessagingTemplate template;
	@Autowired
	TrackStatus trackStatus;
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	@Autowired
	public WebSocketController (SimpMessagingTemplate template){
		this.template = template;
	}

	@Override
	public void handleEvent(ToteEvent event) {
		// An event was fired - we're not really that interested in the tote
		// We want to notify the client of the overall status
		send();
	}
	
	public void send() {
		// TODO Auto-generated method stub
		try {
			logger.info("[WebSocketController::send] Sending:" + trackStatus);
			this.template.convertAndSend("/topic/livestats", trackStatus);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}	

}
