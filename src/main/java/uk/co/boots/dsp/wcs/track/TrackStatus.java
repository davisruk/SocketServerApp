package uk.co.boots.dsp.wcs.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Setter;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.events.EventLogger;
import uk.co.boots.dsp.wcs.events.ToteEvent;

@Component
@Data
public class TrackStatus {
	private int activeTotes;
	private int totalTotes;
	private int totesProcessed;
	private int totesReleased;
	
	private List<String> toteNames = Collections.synchronizedList(new ArrayList<String>());

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
		adjustTotesReleased(true,  false);
	}

	@JsonIgnore
	public synchronized void adjustTotesProcessed (boolean reset, boolean increment) {
		if (reset) totesProcessed = 0;
		else if (increment) totesProcessed ++;
		else totesProcessed --;
		logger.info("[TrackStatus::adjustTotesProcessed] totesProcessed: " + totesProcessed);		
	}
	
	@JsonIgnore
	public synchronized void adjustTotesReleased (boolean reset, boolean increment) {
		if (reset) totesReleased = 0;
		else if (increment) totesReleased ++;
		else totesReleased --;
		logger.info("[TrackStatus::adjustTotesReleased] totesReleased: " + totesReleased);		
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
		logger.info("[TrackStatus::adjustTotalTotes] totalTotes: " + totalTotes);		
	}

}
