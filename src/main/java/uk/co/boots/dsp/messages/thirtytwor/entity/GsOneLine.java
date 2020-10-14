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

@Entity
@Data
@ToString(exclude="gsOneDetail")
@JsonInclude(Include.NON_NULL)
public class GsOneLine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@JsonIgnore
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="gsone_detail_id")
	private GsOneDetail gsOneDetail;
	
	private String lengthOfGSone;
	private String gsOne;
	private char splitIndicator;
}
