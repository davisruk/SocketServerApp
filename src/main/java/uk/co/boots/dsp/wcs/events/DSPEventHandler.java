package uk.co.boots.dsp.wcs.events;

public interface DSPEventHandler {
	public String getName();
	public void setName(String name);
	public boolean handleEvent(ToteEvent event);
	
	default boolean affectsLiveStats() {
		return false;
	};
}
