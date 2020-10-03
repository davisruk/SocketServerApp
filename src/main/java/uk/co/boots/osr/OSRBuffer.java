package uk.co.boots.osr;

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
	
	public boolean isReleasing () {
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
}
