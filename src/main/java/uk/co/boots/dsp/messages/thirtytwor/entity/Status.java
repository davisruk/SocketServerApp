package uk.co.boots.dsp.messages.thirtytwor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
