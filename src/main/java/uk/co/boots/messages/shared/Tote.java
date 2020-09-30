package uk.co.boots.messages.shared;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.Data;
import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.ToteStatusDetail;
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
	
	@OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "tote_identifier_id", referencedColumnName = "id")
	private ToteIdentifier toteIdentifier;

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "transport_id", referencedColumnName = "id")
	private TransportContainer transportContainer;
	
    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "priority_id", referencedColumnName = "id")
	private OrderPriority orderPriority;
	
    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "departure_id", referencedColumnName = "id")
	private DepartureTime departureTime;

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "service_id", referencedColumnName = "id")
	private ServiceCentre serviceCentre;

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "start_time_id", referencedColumnName = "id")
	private StartTime startTime;

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "end_time_id", referencedColumnName = "id")
	private EndTime endTime;

    @OneToOne(cascade={CascadeType.ALL})
    private ToteStatusDetail statusDetail;
	
	@OneToOne(cascade={CascadeType.ALL})
	private OrderDetail orderDetail;

	@Transient
	Calendar startCal;
	@Transient
	Calendar endCal;
	
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
	
	public void setEndTime (EndTime et) {
		endTime = et;
		et.setTote(this);
	}
	
	public void setStartTime (StartTime st) {
		startTime = st;
		st.setTote(this);
	}
	
	public void setStatusDetail (ToteStatusDetail tsd) {
		statusDetail = tsd;
		tsd.setTote(this);
	}

}
