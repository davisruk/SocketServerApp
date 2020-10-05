package uk.co.boots.dsp.events;

import uk.co.boots.dsp.wcs.events.ToteEvent;

public interface DSPEventHandler {
	public void handleEvent(ToteEvent event);
}
