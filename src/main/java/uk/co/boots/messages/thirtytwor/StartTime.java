package uk.co.boots.messages.thirtytwor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import uk.co.boots.messages.BasicRecord;

@Entity
public class StartTime extends BasicRecord {
	public StartTime() {
		super();
		super.setIdentifier('s');
	}
}
