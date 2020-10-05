package uk.co.boots.dsp.comms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component ("dspCommunicationNotifier")
public class DSPCommunicationNotifierImpl implements DSPCommunicationNotifier{
	private List<DSPCommunicationHandler> commsHandlers = new ArrayList<DSPCommunicationHandler>();
	
	@Override
	public void notifyCommunicationHandlers(DSPCommsMessage message) {
		commsHandlers.forEach(handler -> {
			byte[] b = handler.handleCommsForMessage(message);
			message.getRawMessage().setMessageType(message.getRawMessage().getMessageType() + "-" + handler.getTypeExtension());
			message.getTote().addRawMessage(message.getRawMessage());
		});
	}

	@Override
	public void registerDSPCommunicationHandler(DSPCommunicationHandler handler) {
		commsHandlers.add(handler);
	}
}
