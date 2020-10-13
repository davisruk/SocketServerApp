package uk.co.boots.dsp.wcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component ("osrBuffer")
public class OSRBuffer {

	@Autowired
	private OSRConfig osrConfig;
	
	public synchronized void setStarted (boolean started) {
		this.osrConfig.setReleasing(started);
		System.out.println(osrConfig);
	}
	
	public synchronized boolean isReleasing () {
		return osrConfig.isReleasing();
	}
	
	public long getToteTravelTime () {
		return osrConfig.getToteTrackTravelTime();
	}
	
	public synchronized void setOsrConfig (OSRConfig newConfig) {
		System.out.println("-----------------[OSRBuffer][setOsrConfig][START]-----------------");
		System.out.println("-----------------Old Config-----------------");
		System.out.println(osrConfig);
		osrConfig.setToteTrackTravelTime(newConfig.getToteTrackTravelTime());
		osrConfig.setMaxTotesOnTrack(newConfig.getMaxTotesOnTrack());
		osrConfig.setToteReleaseInterval(newConfig.getToteReleaseInterval());
		osrConfig.setReleasing(newConfig.isReleasing());
		osrConfig.setIncludeFMD(newConfig.isIncludeFMD());
		osrConfig.setSendThirtyTwoRShort(newConfig.isSendThirtyTwoRShort());
		System.out.println("-----------------New Config-----------------");
		System.out.println(osrConfig);
		System.out.println("-----------------[OSRBuffer][setOsrConfig][END]-----------------");
		
	}

	public long getToteReleaseInterval() {
		return osrConfig.getToteReleaseInterval();
	}

	public int getTrackToteCapacity() {
		return osrConfig.getMaxTotesOnTrack();
	}
	
	public boolean processingFMD () {
		return osrConfig.isIncludeFMD();
	}
	
	public boolean sendThirtyTwoRShort() {
		return osrConfig.isSendThirtyTwoRShort();
	}
	
	public void setSendThirtyTwoRShort(boolean val) {
		osrConfig.setSendThirtyTwoRShort(val);
	}
	
}
