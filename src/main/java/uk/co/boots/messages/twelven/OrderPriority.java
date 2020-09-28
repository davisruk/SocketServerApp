package uk.co.boots.messages.twelven;

import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;

@Entity
public class OrderPriority extends BasicRecord {
	public OrderPriority () {
		super ();
		super.setIdentifier('U');	
	}
	
}
