package uk.co.boots.dsp.wcs.rules.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.base.entity.OrderLine;
import uk.co.boots.dsp.messages.thirtytwor.serialization.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.wcs.events.handlers.EventLogger;
import uk.co.boots.dsp.wcs.rules.RuleParameters;
import uk.co.boots.dsp.wcs.rules.RuleProcessor;

@Component
public class NotSuppliedProcessor implements RuleProcessor {

	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	@Override
	public boolean canHandle(String messageType) {
		return messageType.equals(RuleParameters.NOT_SUPPLIED);
	}

	public void process (OrderLine line, int index) {
		logger.info("[NotSuppliedProcessor::process] " + "setting not supplied on line - " + line.getId());
		line.setStatus(RuleParameters.NOT_SUPPLIED_STATUS);
		line.setNumberOfPacks(OrderLineArrayListSerializationControl.formatNumPacks(0));
		line.setNumberOfPills(OrderLineArrayListSerializationControl.formatNumPills(0));
	}
}
