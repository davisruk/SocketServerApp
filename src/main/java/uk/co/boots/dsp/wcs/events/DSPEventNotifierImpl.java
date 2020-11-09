package uk.co.boots.dsp.wcs.events;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component ("dspEventNotifier")
public class DSPEventNotifierImpl implements DSPEventNotifier {
	@Autowired
	private List<DSPEventHandler> eventHandlers;
	
	public void registerEventHandler (DSPEventHandler newHandler) {
		int i = IntStream.range(0, eventHandlers.size())
	    .filter(handlerInd-> eventHandlers.get(handlerInd).getName().equals(newHandler.getName()))
	    .findFirst()
	    .orElse(-1);
		if (i > -1) {
			eventHandlers.set(i,  newHandler);
		} else {
			eventHandlers.add(newHandler);			
		}


	}

	public void notifyEventHandlers (ToteEvent evt ) {
		eventHandlers.forEach(handler -> handler.handleEvent(evt));
	}

	@Override
	public void resetHandlers() {
		eventHandlers = new ArrayList<DSPEventHandler>();
	}
}
