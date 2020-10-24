package uk.co.boots.dsp.wcs.events;

public interface DSPEventHandler {
	public String getName();
	public void setName(String name);
	public void handleEvent(ToteEvent event);
}
