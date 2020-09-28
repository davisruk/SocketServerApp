package uk.co.boots.messages.shared;

import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;

@Entity
public class ToteIdentifier extends BasicRecord {
	public ToteIdentifier(){
		super();
		super.setIdentifier('T');
	}
}
