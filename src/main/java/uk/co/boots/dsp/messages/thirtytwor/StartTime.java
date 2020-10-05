package uk.co.boots.dsp.messages.thirtytwor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.shared.Tote;

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
