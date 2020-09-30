package uk.co.boots.messages.thirtytwor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.shared.Tote;

@Entity
@Data
@ToString(exclude="tote")
public class StartTime extends BasicRecord {

	@OneToOne(mappedBy = "startTime")
	private Tote tote;
	
	public StartTime() {
		super();
		super.setIdentifier('s');
	}
}
