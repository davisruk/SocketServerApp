package uk.co.boots.dsp.messages.base.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.framework.entity.BasicRecord;
@Entity
@Data
@ToString(exclude="tote")
@JsonInclude(Include.NON_NULL)
public class TransportContainer extends BasicRecord {

	@JsonIgnore
	@OneToOne(mappedBy = "transportContainer")
    private Tote tote;

	
	public TransportContainer () {
		super();
		super.setIdentifier('C');
	}	
}
