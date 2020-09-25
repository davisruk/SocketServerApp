package uk.co.boots.messages.thirtytwor;

import java.util.ArrayList;

import lombok.Data;

@Data
public class StatusArrayList extends ArrayList<Status> {

	private int numberOfLines;
	private int statusLength;
	
	public int getStatusOffset() {
		// we don't count numberOfLines
		return 0;
	}
		
}
