package uk.co.boots.dsp.messages.twelven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.dsp.messages.shared.HeaderSerializationControl;

@Component
@Getter
public class TwelveNSerializationControl {

	public static final char TOTE_ID = 'T';
	public static final char CONTAINER_ID = 'C';
	public static final char ORDER_PRIORITY = 'U';
	public static final char SERVICE_CENTRE = 'E';
	public static final char DEPARTURE_TIME = 'e';
	public static final char ORDER_LIST = 'X';
	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private OrderLineArrayListSerializationControl orderLineArrayListSerializationControl;

	

}
