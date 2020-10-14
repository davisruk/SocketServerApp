package uk.co.boots.dsp.messages.base.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import uk.co.boots.dsp.messages.framework.entity.BasicMessage;
import uk.co.boots.dsp.messages.thirtytwor.entity.EndTime;
import uk.co.boots.dsp.messages.thirtytwor.entity.StartTime;
import uk.co.boots.dsp.messages.thirtytwor.entity.ToteStatusDetail;
import uk.co.boots.dsp.messages.twelven.entity.DepartureTime;
import uk.co.boots.dsp.messages.twelven.entity.OrderPriority;
import uk.co.boots.dsp.messages.twelven.entity.ServiceCentre;

@Data
@Entity
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "header", "toteIdentifier", "transportContainer", "orderPriority", "departureTime", "serviceCentre", "startTime", "endTime", "statusDetail", "orderDetail" })
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

	@JsonIgnore
	@OneToMany(mappedBy="tote",cascade={CascadeType.ALL})
	private List<RawMessage> messageList;
	
	@JsonIgnore
	boolean processed;
	
	@JsonIgnore
	@Transient
	Calendar startCal;
	@JsonIgnore
	@Transient
	Calendar endCal;
	
	public Tote() {
		messageList = new ArrayList<RawMessage>();
		processed = false;
	}
	
	@Override
	@Transient
	@JsonIgnore
	public boolean hasResponse() {
		return true;
	}

	@Override
	@Transient
	@JsonIgnore
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

	public void addRawMessage(byte[] bytes, String messageType, Date creationTime) {
		RawMessage rm = new RawMessage(messageType, new String(bytes), creationTime);
		addRawMessage(rm);
	}
	
	public void addRawMessage(RawMessage message) {
		message.setTote(this);
		messageList.add(message);
	}
	
}
