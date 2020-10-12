package uk.co.boots.dsp.wcs.rules.processors;

import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.thirtytwor.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.wcs.rules.RuleParameters;
import uk.co.boots.dsp.wcs.rules.RuleProcessor;

@Component
public class NotSuppliedProcessor implements RuleProcessor {

	@Override
	public boolean canHandle(String messageType) {
		return messageType.equals(RuleParameters.NOT_SUPPLIED);
	}

	public void process (OrderLine line) {
		System.out.println("Setting not supplied on line - " + line.getId());
		line.setStatus(RuleParameters.NOT_SUPPLIED_STATUS);
		line.setNumberOfPacks(OrderLineArrayListSerializationControl.formatNumPacks(0));
		line.setNumberOfPills(OrderLineArrayListSerializationControl.formatNumPills(0));
	}
}
