package uk.co.boots.dsp.wcs.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ToteActivationHandler extends DSPEventHandlerAdapter {
	public ToteActivationHandler() {
		super("ToteActivationHandler");
	}

	@Autowired
	private TrackStatus trackStatus;

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
	}
}