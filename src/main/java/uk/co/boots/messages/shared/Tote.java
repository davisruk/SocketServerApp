package uk.co.boots.messages.shared;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.Data;
import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.StatusArrayList;
import uk.co.boots.messages.twelven.DepartureTime;
import uk.co.boots.messages.twelven.OrderPriority;
import uk.co.boots.messages.twelven.ServiceCentre;

@Data
@Entity
public class Tote implements BasicMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Embedded
    private Header header;
	@OneToOne(mappedBy = "tote", cascade = CascadeType.ALL)
	private ToteIdentifier toteIdentifier;
	@OneToOne(mappedBy = "tote", cascade = CascadeType.ALL)
	private TransportContainer transportContainer;
	@OneToOne(mappedBy = "tote", cascade = CascadeType.ALL)
	private OrderPriority orderPriority;
	@OneToOne(mappedBy = "tote", cascade = CascadeType.ALL)
	private DepartureTime departureTime;
	@OneToOne(mappedBy = "tote", cascade = CascadeType.ALL)
	private ServiceCentre serviceCentre;
	@Transient
	private StartTime startTime;
	@Transient
	private EndTime endTime;
	@Transient
	private StatusArrayList status;
	@Transient
	private OrderLineArrayList orderLines;

	@Override
	@Transient
	public boolean hasResponse() {
		return true;
	}

	@Override
	@Transient
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return toString().getBytes();
	}
}
