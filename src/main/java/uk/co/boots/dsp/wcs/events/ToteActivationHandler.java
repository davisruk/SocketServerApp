package uk.co.boots.dsp.wcs.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ToteActivationHandler extends DSPEventHandlerAdapter {
	
	@Autowired
	private WebSocketController webSocketController;
	
	@Autowired
	private TrackStatus trackStatus;
	
	public ToteActivationHandler() {
		super("ToteActivationHandler");
	}

	@Override
	public void handleEvent(ToteEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEventType()) {
			case TOTE_ACTIVATED:
				trackStatus.adjustActiveTotes(false,  true, event);
				break;
			case TOTE_DEACTIVATED:
				trackStatus.adjustActiveTotes(false,  false, event);
				break;
			default:
				break;
		}
		webSocketController.send();
	}
}