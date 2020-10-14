package uk.co.boots.dsp.messages.twelven.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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
public class OrderPriority extends BasicRecord {

	@JsonIgnore
	@OneToOne(mappedBy = "orderPriority")
    private Tote tote;
	
	public OrderPriority () {
		super ();
		super.setIdentifier('U');	
	}
	
}
