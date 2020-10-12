package uk.co.boots.dsp.wcs.rules;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RuleParameterList {
	private List<RuleParameters> lines = new ArrayList<RuleParameters>();
}
