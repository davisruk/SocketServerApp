package uk.co.boots.osr;

import org.springframework.stereotype.Component;

@Component
public interface DSPCommunicationNotifier {
	public void notifyCommunicationHandlers (DSPCommsMessage message);
	public void registerDSPCommunicationHandler (DSPCommunicationHandler handler);
}
