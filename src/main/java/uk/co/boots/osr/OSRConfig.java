package uk.co.boots.osr;

import lombok.Data;

@Data
public class OSRConfig {
	private long toteReleaseInterval;
	private long toteTrackTravelTime;
	private int maxTotesOnTrack;
	private boolean releasing;
}
