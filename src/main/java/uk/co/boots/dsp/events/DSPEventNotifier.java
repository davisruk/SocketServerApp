package uk.co.boots.dsp.events;

import org.springframework.stereotype.Component;

import uk.co.boots.dsp.wcs.events.ToteEvent;

@Component
public interface DSPEventNotifier {
	public void notifyEventHandlers (ToteEvent event);
	public void registerEventHandler (DSPEventHandler handler);
}
