package uk.co.boots.osr;

import java.util.ArrayDeque;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component ("osrBuffer")
public class OSRBuffer {

	@Getter
	private ArrayDeque<String> totes = new ArrayDeque<String>();
	@Getter
	@Autowired
	OSRConfig osrConfig;
	
	public synchronized void setStarted (boolean started) {
		System.out.println(osrConfig);
		this.osrConfig.setReleasing(started);
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
	
	public void addTote(String twelveN) {
		totes.add(twelveN);
	}
}
