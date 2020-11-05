package uk.co.boots.dsp.wcs.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component ("dspEventNotifier")
public class DSPEventNotifierImpl implements DSPEventNotifier {
	@Autowired
	private List<DSPEventHandler> eventHandlers;
	
	public void notifyEventHandlers (ToteEvent evt ) {
		eventHandlers.forEach(handler -> handler.handleEvent(evt));
	}
}
