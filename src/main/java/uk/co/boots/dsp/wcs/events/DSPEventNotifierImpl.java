package uk.co.boots.dsp.wcs.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.websocket.WebSocketController;
import uk.co.boots.dsp.wcs.osr.OSRBuffer;

@Component ("dspEventNotifier")
public class DSPEventNotifierImpl implements DSPEventNotifier {
	@Autowired
	private List<DSPEventHandler> eventHandlers;
	@Autowired
	private WebSocketController webSocketController;
	
	public void notifyEventHandlers (ToteEvent evt ) {
		eventHandlers.forEach(handler -> {
			if (handler.handleEvent(evt) && handler.affectsLiveStats()) {
				webSocketController.sendEvent(evt);
			}
		});
	}
}
