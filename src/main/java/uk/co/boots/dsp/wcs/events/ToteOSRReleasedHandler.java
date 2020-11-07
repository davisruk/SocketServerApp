package uk.co.boots.dsp.wcs.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ToteOSRReleasedHandler extends DSPEventHandlerAdapter {
	@Autowired
	private WebSocketController webSocketController;
	
	@Autowired
	private TrackStatus trackStatus;
	
	public ToteOSRReleasedHandler() {
		super("ToteOSRReleasedHandler");
	}

	@Override
	public void handleEvent(ToteEvent event) {
		// TODO Auto-generated method stub
		if (event.getEventType() == ToteEvent.EventType.TOTE_RELEASED_FROM_OSR){
				trackStatus.adjustTotesReleased(false,  true);
				webSocketController.send();
		}
	}
}
