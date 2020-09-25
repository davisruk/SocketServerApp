package uk.co.boots.messages.shared;

import java.util.ArrayList;

import lombok.Data;
import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.StatusArrayList;
import uk.co.boots.messages.twelven.DepartureTime;
import uk.co.boots.messages.twelven.OrderPriority;
import uk.co.boots.messages.twelven.ServiceCentre;

@Data
public class ToteMessage implements BasicMessage {
	private Header header;
	private ToteIdentifier toteIdentifier;
	private TransportContainer transportContainer;
	private OrderPriority orderPriority;
	private DepartureTime departureTime;
	private ServiceCentre serviceCentre;
	private StartTime startTime;
	private EndTime endTime;
	private StatusArrayList status;
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
