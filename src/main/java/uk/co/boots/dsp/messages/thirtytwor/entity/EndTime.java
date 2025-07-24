package uk.co.boots.dsp.messages.thirtytwor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.messages.framework.entity.BasicRecord;

@Entity
@Data
@ToString(exclude="tote")
@JsonInclude(Include.NON_NULL)
public class EndTime extends BasicRecord {
	
	@JsonIgnore
	@OneToOne(mappedBy = "endTime")
    private Tote tote;
	
	public EndTime() {
		super();
		super.setIdentifier('e');
	}

}
