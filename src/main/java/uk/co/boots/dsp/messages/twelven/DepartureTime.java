package uk.co.boots.dsp.messages.twelven;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.shared.Tote;

@Entity
@Data
@ToString(exclude="tote")
public class DepartureTime extends BasicRecord {

	@OneToOne(mappedBy = "departureTime")
    private Tote tote;

	public DepartureTime() {
		super();
		super.setIdentifier('e');
	}

}
