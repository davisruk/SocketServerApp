package uk.co.boots.messages.twelven;

import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;

@Entity
public class DepartureTime extends BasicRecord {
	public DepartureTime() {
		super();
		super.setIdentifier('e');
	}

}
