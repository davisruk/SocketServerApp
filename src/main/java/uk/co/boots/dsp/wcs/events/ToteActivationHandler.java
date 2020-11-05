package uk.co.boots.dsp.wcs.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.wcs.osr.OSRBuffer;
import uk.co.boots.dsp.wcs.track.TrackStatus;

@Component
public class ToteActivationHandler extends DSPEventHandlerAdapter {
	public ToteActivationHandler() {
		super("ToteActivationHandler");
	}

	@Autowired
	private TrackStatus trackStatus;

	@Override
	public boolean handleEvent(ToteEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEventType()) {
			case TOTE_ACTIVATED:
				trackStatus.adjustActiveTotes(false,  true, event);
				return true;
			case TOTE_DEACTIVATED:
				trackStatus.adjustActiveTotes(false,  false, event);
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public boolean affectsLiveStats() {
		return true;
	}
}