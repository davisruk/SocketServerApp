package uk.co.boots.osr;

import org.springframework.stereotype.Component;

@Component
public interface DSPEventNotifier {
	public void notifyEventHandlers (ToteEvent event);
	public void registerEventHandler (DSPEventHandler handler);
}
