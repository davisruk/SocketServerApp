package uk.co.boots.dsp.wcs.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import uk.co.boots.dsp.wcs.events.EventLogger;

@Component
@Getter
public class TrackStatus {
	private int activeTotes;
	private int totalTotes;
	private int totesProcessed;
	@Setter
	private String receiveChannelClient;
	@Setter
	private String sendChannelClient;
	
	@JsonIgnore
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	@JsonIgnore
	public synchronized void adjustTotesProcessed (boolean reset, boolean increment) {
		if (reset) totesProcessed = 0;
		else if (increment) totesProcessed ++;
		else totesProcessed --;
		logger.info("[TrackStatus::adjustTotesProcessed] totesProcessed: " + totesProcessed);		
	}
	
	public int getActiveTotes() {
		return activeTotes;
	}
	@JsonIgnore
	public synchronized void adjustActiveTotes(boolean reset, boolean increment) {
		if (reset) activeTotes = 0;
		else if (increment) activeTotes ++;
		else activeTotes --;
		logger.info("[TrackStatus::adjustActiveTotes] Active Totes: " + activeTotes);		
	}
	@JsonIgnore
	public synchronized void adjustTotalTotes (boolean reset, boolean increment) {
		if (reset) totalTotes = 0;
		else if (increment) totalTotes ++;
		else totalTotes --;
		logger.info("[TrackStatus::adjustTotalTotes] totesProcessed: " + totesProcessed);		
	}
	

}
