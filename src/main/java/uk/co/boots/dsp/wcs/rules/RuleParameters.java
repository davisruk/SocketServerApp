package uk.co.boots.dsp.wcs.rules;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@Entity
@JsonInclude(Include.NON_DEFAULT)
public class RuleParameters {
	public static final String SHORT_SUPPLIED = "shortSupplied";
	public static final String NOT_SUPPLIED = "notSupplied";
	public static final String INDETERMINATE = "indeterminate";
	
	public static final String NOT_SUPPLIED_STATUS = "35";
	public static final String SHORT_SUPPLIED_STATUS = "40";
	public static final String INDETERMINATE_STATUS = "50";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String productCode;
	private String ruleType;
}
