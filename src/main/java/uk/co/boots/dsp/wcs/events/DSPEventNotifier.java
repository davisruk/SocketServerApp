package uk.co.boots.dsp.wcs.events;

import org.springframework.stereotype.Component;

@Component
public interface DSPEventNotifier {
	public void notifyEventHandlers (ToteEvent event);
	public void registerEventHandler (DSPEventHandler handler);
	public void resetHandlers();
}
