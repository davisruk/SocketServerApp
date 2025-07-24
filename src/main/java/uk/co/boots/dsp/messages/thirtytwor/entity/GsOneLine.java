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
