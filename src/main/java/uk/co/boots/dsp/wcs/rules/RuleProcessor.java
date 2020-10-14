package uk.co.boots.dsp.wcs.rules;

import uk.co.boots.dsp.messages.base.entity.OrderLine;

public interface RuleProcessor {
	public boolean canHandle(String messageType);
	public void process(OrderLine line, int indexInOrderLines);
}
