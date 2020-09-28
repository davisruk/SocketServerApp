package uk.co.boots.messages.thirtytwor;

import javax.persistence.DiscriminatorValue;

import uk.co.boots.messages.BasicRecord;

public class EndTime extends BasicRecord {
	public EndTime() {
		super();
		super.setIdentifier('e');
	}

}
