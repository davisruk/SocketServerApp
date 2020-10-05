package uk.co.boots.dsp.comms;

import org.springframework.stereotype.Component;

@Component
public interface DSPCommunicationNotifier {
	public void notifyCommunicationHandlers (DSPCommsMessage message);
	public void registerDSPCommunicationHandler (DSPCommunicationHandler handler);
	public void replaceDSPCommunicationHandler (DSPCommunicationHandler oldHandler, DSPCommunicationHandler newHandler);
	public void sendFailedMessages (DSPCommunicationHandler handler);
}
