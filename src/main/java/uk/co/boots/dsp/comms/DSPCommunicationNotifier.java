package uk.co.boots.dsp.comms;

import org.springframework.stereotype.Component;

@Component
public interface DSPCommunicationNotifier {
	public void notifyCommunicationHandlers (DSPCommsMessage message);
	public void registerDSPCommunicationHandler (DSPCommunicationHandler handler);
}
