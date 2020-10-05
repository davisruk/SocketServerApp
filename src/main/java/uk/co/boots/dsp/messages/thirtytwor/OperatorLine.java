package uk.co.boots.dsp.messages.thirtytwor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="operatorDetail")
public class OperatorLine {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="operator_detail_id")
	private OperatorDetail operatorDetail;
	
	private String operatorId;
	private String roleId;
	private String timestamp;
}
