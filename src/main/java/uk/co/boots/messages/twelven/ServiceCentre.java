package uk.co.boots.messages.twelven;

import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;

@Entity
public class ServiceCentre extends BasicRecord {
	public ServiceCentre() {
		super();
		super.setIdentifier('E');
	}
}
