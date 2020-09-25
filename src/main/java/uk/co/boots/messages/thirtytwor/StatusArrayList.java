package uk.co.boots.messages.thirtytwor;

import java.util.ArrayList;

public class StatusArrayList extends ArrayList<Status> {

	private int numberOfLines;
	private int statusLength;
	
	public int getStatusOffset() {
		// we don't count numberOfLines
		return 0;
	}
		
}
