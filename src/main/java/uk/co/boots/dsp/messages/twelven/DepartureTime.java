package uk.co.boots.dsp.messages.twelven;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.shared.Tote;

@Entity
@Data
@ToString(exclude="tote")
@JsonInclude(Include.NON_NULL)
public class DepartureTime extends BasicRecord {

	@JsonIgnore
	@OneToOne(mappedBy = "departureTime")
    private Tote tote;

	public DepartureTime() {
		super();
		super.setIdentifier('e');
	}

}
