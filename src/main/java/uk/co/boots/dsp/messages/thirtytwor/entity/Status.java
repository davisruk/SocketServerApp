package uk.co.boots.dsp.messages.thirtytwor.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="statusDetail")
@Entity
@JsonInclude(Include.NON_NULL)
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@JsonIgnore
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="status_detail_id")
	private ToteStatusDetail statusDetail;
	
	private String status;

}
