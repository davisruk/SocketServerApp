package uk.co.boots.messages.twelven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.shared.HeaderSerializationControl;

@Component
@Getter
public class TwelveNSerializationControl {

	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private OrderLineArrayListSerializationControl orderLineArrayListSerializationControl;

	

}
