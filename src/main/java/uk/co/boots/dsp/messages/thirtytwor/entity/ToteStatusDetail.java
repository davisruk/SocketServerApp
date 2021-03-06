package uk.co.boots.dsp.messages.thirtytwor.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.messages.framework.serialization.SerializationControlIdentifiers;

@Data
@ToString(exclude="tote")
@Entity
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "identifier", "numberOfLines", "statusLength", "statusList" })
public class ToteStatusDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@JsonIgnore
	@OneToOne
    private Tote tote;
	
	private char identifier = SerializationControlIdentifiers.STATUS;
	private int numberOfLines;
	private int statusLength;
	
	@OneToMany(mappedBy="statusDetail",cascade={CascadeType.ALL})
	private List<Status> statusList;
	
	public ToteStatusDetail() {
		statusList = new ArrayList<Status>();
	}
	
	@JsonIgnore
	public int getStatusOffset() {
		// we don't count numberOfLines
		return 0;
	}
	
	public void addStatus(Status status) {
		statusList.add(status);
		status.setStatusDetail(this);
	}
		
}
