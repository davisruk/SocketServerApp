package uk.co.boots.dsp.wcs.osr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.wcs.events.handlers.EventLogger;

@Component ("osrBuffer")
public class OSRBuffer {

	@Autowired
	private OSRConfig osrConfig;
	
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	public synchronized void setStarted (boolean started) {
		this.osrConfig.setReleasing(started);
	}
	
	public synchronized boolean isReleasing () {
		return osrConfig.isReleasing();
	}
	
	public long getToteTravelTime () {
		return osrConfig.getToteTrackTravelTime();
	}
	
	public synchronized void setOsrConfig (OSRConfig newConfig) {
		
		osrConfig.setToteTrackTravelTime(newConfig.getToteTrackTravelTime());
		osrConfig.setMaxTotesOnTrack(newConfig.getMaxTotesOnTrack());
		osrConfig.setToteReleaseInterval(newConfig.getToteReleaseInterval());
		osrConfig.setReleasing(newConfig.isReleasing());
		osrConfig.setIncludeFMD(newConfig.isIncludeFMD());
		osrConfig.setSendThirtyTwoRShort(newConfig.isSendThirtyTwoRShort());
		logger.info("[OSRBuffer::setOsrConfig] New OSR Config : " + osrConfig);
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
	
	public OSRConfig getOSRConfig() {
		return osrConfig;
	}
}
