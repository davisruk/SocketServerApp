package uk.co.boots.dsp.wcs.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component ("dspEventNotifier")
public class DSPEventNotifierImpl implements DSPEventNotifier {

	private List<DSPEventHandler> eventHandlers = new ArrayList<DSPEventHandler>();
	
	public void registerEventHandler (DSPEventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public void notifyEventHandlers (ToteEvent evt ) {
		eventHandlers.forEach(handler -> handler.handleEvent(evt));
	}

	@Override
	public void resetHandlers() {
		eventHandlers = new ArrayList<DSPEventHandler>();
	}
}
