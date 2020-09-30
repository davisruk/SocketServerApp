package uk.co.boots.osr;

import uk.co.boots.messages.shared.Tote;

public interface ToteEventHandler {
	public void handleToteActivation(Tote tote);
	public void handleToteDeactivation(Tote tote);
}
