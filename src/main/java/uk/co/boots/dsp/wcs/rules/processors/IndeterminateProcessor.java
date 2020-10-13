package uk.co.boots.dsp.wcs.rules.processors;

import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.thirtytwor.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.wcs.rules.RuleParameters;
import uk.co.boots.dsp.wcs.rules.RuleProcessor;

public class IndeterminateProcessor implements RuleProcessor {

	@Override
	public boolean canHandle(String messageType) {
		return messageType.equals(RuleParameters.INDETERMINATE);
	}

	public void process (OrderLine line, int index) {
		line.setStatus(RuleParameters.INDETERMINATE_STATUS);
		line.setNumberOfPacks(OrderLineArrayListSerializationControl.formatNumPacks(0));
		line.setNumberOfPills(OrderLineArrayListSerializationControl.formatNumPills(0));
	}
}
