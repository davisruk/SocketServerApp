package uk.co.boots.dsp.wcs.osr;

import lombok.Data;

@Data
public class OSRConfig {
	private long toteReleaseInterval;
	private long toteTrackTravelTime;
	private int maxTotesOnTrack;
	private boolean releasing;
	private boolean includeFMD;
	private boolean sendThirtyTwoRShort;
}
