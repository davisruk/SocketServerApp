package uk.co.boots.dsp.wcs.rules;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleProcessorFactory {
	@Autowired
	private List<RuleProcessor> ruleProcessors;
	
	public Optional<RuleProcessor> getProcessor(String ruleType) {
		return ruleProcessors.stream().filter(service -> service.canHandle(ruleType)).findFirst();
	}
}
