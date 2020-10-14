package uk.co.boots.dsp.messages.thirtytwor.serialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.dsp.messages.base.serialization.HeaderSerializationControl;

@Component
@Getter
public class ThirtyTwoRSerializationControl {
	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private OrderLineArrayListSerializationControl thirtyTwoROrderLineArrayListSerializationControl;

}
