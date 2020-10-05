package uk.co.boots.dsp.wcs.events;

import uk.co.boots.dsp.messages.shared.Tote;

public interface ToteEventHandler {
	public void handleToteActivation(Tote tote);
	public void handleToteDeactivation(Tote tote);
}
