package uk.co.boots.dsp.wcs.rules.processors;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.thirtytwor.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.wcs.rules.RuleParameters;
import uk.co.boots.dsp.wcs.rules.RuleProcessor;

@Component
public class ShortSuppliedProcessor implements RuleProcessor {

	@Override
	public boolean canHandle(String messageType) {
		return messageType.equals(RuleParameters.SHORT_SUPPLIED);
	}

	public void process (OrderLine line, int index) {
		OrderDetail od = line.getOrderDetail();
		if (isLastOrderLineForProduct(od, line.getProductId(), index)) {
			line.setStatus(RuleParameters.SHORT_SUPPLIED_STATUS);
			line.setNumberOfPacks(OrderLineArrayListSerializationControl.formatNumPacks(0));
			line.setNumberOfPills(OrderLineArrayListSerializationControl.formatNumPills(0));
		}
	}
	
	private boolean isLastOrderLineForProduct(OrderDetail od, String productId, int currentIndex) {
		int lastIndex = -1;
		List<OrderLine> ol = od.getOrderLines();
		for (int i = 0; i < ol.size(); i++) {
			if (ol.get(i).getProductId().equals(productId))
				lastIndex = i;
		}
		
		return lastIndex == currentIndex;
	}
}
