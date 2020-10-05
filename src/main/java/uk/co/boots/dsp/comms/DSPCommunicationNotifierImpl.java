package uk.co.boots.dsp.comms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.boots.dsp.wcs.exceptions.DSPMessageException;

@Component ("dspCommunicationNotifier")
public class DSPCommunicationNotifierImpl implements DSPCommunicationNotifier{
	private List<DSPCommunicationHandler> commsHandlers = new ArrayList<DSPCommunicationHandler>();
	private List<DSPCommsMessage> failedMessages = new ArrayList<DSPCommsMessage>();

	@Override
	public void notifyCommunicationHandlers(DSPCommsMessage message) {
		commsHandlers.forEach(handler -> {
			try {
				handler.handleCommsForMessage(message);
			} catch (DSPMessageException ioe) {
				failedMessages.add(message);
			}
			// continue processing
			message.getRawMessage().setMessageType(message.getRawMessage().getMessageType() + "-" + handler.getTypeExtension());
			message.getTote().addRawMessage(message.getRawMessage());
		});
	}

	@Override
	public void registerDSPCommunicationHandler(DSPCommunicationHandler handler) {
		commsHandlers.add(handler);
	}

	@Override
	public void replaceDSPCommunicationHandler(DSPCommunicationHandler oldHandler, DSPCommunicationHandler newHandler) {
		int i = commsHandlers.indexOf(oldHandler);
		if (oldHandler == null || i == -1) {
			registerDSPCommunicationHandler(newHandler);
		} else {
			commsHandlers.set(i, newHandler);
		}
	}
	
	public synchronized void sendFailedMessages (DSPCommunicationHandler handler) {
		List<DSPCommsMessage> newFails = new ArrayList<DSPCommsMessage>();  
		failedMessages.forEach(message -> {
			try {
				byte[] b = handler.handleCommsForMessage(message);
				message.getRawMessage().setMessageType(message.getRawMessage().getMessageType() + "-" + handler.getTypeExtension());
				message.getTote().addRawMessage(message.getRawMessage());
			} catch (DSPMessageException dspme) {
				newFails.add(message);
			}
		});
		failedMessages = newFails;
	}

}
