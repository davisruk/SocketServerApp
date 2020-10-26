package uk.co.boots.dsp.wcs.track;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;

@Component
@Getter
public class TrackStatus {
	private int activeTotes;
	private int totalTotes;
	private int totesProcessed;
	private ArrayList<String> toteNames = new ArrayList<String>();

	@Setter
	private String receiveChannelClient;
	@Setter
	private String sendChannelClient;
	
	@JsonIgnore
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);

	
	@JsonIgnore
	public synchronized void resetStatus() {
		adjustActiveTotes(true, false, null);
		adjustTotalTotes(true,  false);
		adjustTotesProcessed(true,  false);
	}

	@JsonIgnore
	public synchronized void adjustTotesProcessed (boolean reset, boolean increment) {
		if (reset) totesProcessed = 0;
		else if (increment) totesProcessed ++;
		else totesProcessed --;
		logger.info("[TrackStatus::adjustTotesProcessed] totesProcessed: " + totesProcessed);		
	}
	
	@JsonIgnore
	public synchronized void adjustActiveTotes(boolean reset, boolean increment, ToteEvent event) {
		
		if (reset) {
			activeTotes = 0;
			toteNames.clear();
			return;
		}
		
		Tote t = event.getTote();
		
		if (increment) {
			activeTotes ++;
			toteNames.add(t.getHeader().getOrderId() +  ":" + t.getHeader().getSheetNumber() + ":" + t.getId());
			toteNames.removeIf(name -> name.equals("None"));
		}
		else {
			activeTotes --;
			toteNames.removeIf(name -> name.equals(t.getHeader().getOrderId() +  ":" + t.getHeader().getSheetNumber() + ":" + t.getId()));
			if (toteNames.isEmpty()) {
				toteNames.add("None");
			}
		}
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
