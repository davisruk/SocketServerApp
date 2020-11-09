package uk.co.boots.dsp.wcs.events.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.events.DSPEventHandlerAdapter;
import uk.co.boots.dsp.wcs.events.ToteEvent;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ToteActivationHandler extends DSPEventHandlerAdapter {
	@Autowired
	private TrackStatus trackStatus;
	@Autowired
	private WebSocketController webSocketController;
	
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