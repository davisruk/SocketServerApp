package uk.co.boots.dsp.wcs.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ResetRunHandler extends DSPEventHandlerAdapter {
	@Autowired
	private WebSocketController webSocketController;
	
	Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	@Autowired
	private TrackStatus trackStatus;
	
	public ResetRunHandler() {
		super("ResetRunHandler");
	}

	@Override
	public void handleEvent(ToteEvent event) {
		// TODO Auto-generated method stub
		if (event.getEventType() == ToteEvent.EventType.RESET_RUN){
				trackStatus.resetStatus();
				logger.debug("[ResetRunHandler::handleEvent::RESET_RUN] " + trackStatus);				
				webSocketController.send();
		}
	}
}