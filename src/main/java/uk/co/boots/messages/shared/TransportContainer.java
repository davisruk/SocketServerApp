package uk.co.boots.messages.shared;

import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;
@Entity
public class TransportContainer extends BasicRecord {
	public TransportContainer () {
		super();
		super.setIdentifier('C');
	}	
}
