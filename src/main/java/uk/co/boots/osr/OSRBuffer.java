package uk.co.boots.osr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component ("osrBuffer")
public class OSRBuffer {

	@Getter
	private List<String> totes = new ArrayList<String>();
	@Getter
	private boolean started = false;
	
	public synchronized void setStarted (boolean started) {
		this.started = started;
	}
	
	public void addTote(String twelveN) {
		totes.add(twelveN);
	}
	
	
}
