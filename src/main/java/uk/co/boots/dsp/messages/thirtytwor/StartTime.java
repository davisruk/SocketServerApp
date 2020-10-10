package uk.co.boots.dsp.messages.thirtytwor;

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
public class StartTime extends BasicRecord {

	@JsonIgnore
	@OneToOne(mappedBy = "startTime")
	private Tote tote;
	
	public StartTime() {
		super();
		super.setIdentifier('s');
	}
}
