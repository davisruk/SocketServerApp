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
@ToString(exclude="gsOneDetail")

public class GsOneLine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="gsone_detail_id")
	private GsOneDetail gsOneDetail;
	
	private String lengthOfGSone;
	private String gsOne;
	private char splitIndicator;
}
