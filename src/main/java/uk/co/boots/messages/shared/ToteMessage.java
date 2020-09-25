package uk.co.boots.messages.twelven;

import java.util.ArrayList;

import lombok.Data;
import uk.co.boots.messages.BasicMessage;

@Data
public class TwelveN implements BasicMessage {
	private Header header;
	private ToteIdentifier toteIdentifier;
	private TransportContainer transportContainer;
	private OrderPriority orderPriority;
	private DepartureTime departureTime;
	private ServiceCentre serviceCentre;
	private OrderLineArrayList orderLines;

	@Override
	public boolean hasResponse() {
		return true;
	}

	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return toString().getBytes();
	}
}
