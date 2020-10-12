package uk.co.boots.dsp.wcs.rules;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@Entity
@JsonInclude(Include.NON_NULL)
public class RuleParameters {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String productCode;
	private String ruleType;
}
