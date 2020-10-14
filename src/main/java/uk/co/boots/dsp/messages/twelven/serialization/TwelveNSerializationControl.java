package uk.co.boots.dsp.messages.twelven.serialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.dsp.messages.base.serialization.HeaderSerializationControl;

@Component
@Getter
public class TwelveNSerializationControl {

	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private OrderLineArrayListSerializationControl orderLineArrayListSerializationControl;

	

}
