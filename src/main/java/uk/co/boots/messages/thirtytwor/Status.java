package uk.co.boots.messages.thirtytwor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="statusDetail")
@Entity
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="status_detail_id")
	private ToteStatusDetail statusDetail;
	
	private String status;

}
