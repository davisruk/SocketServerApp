package uk.co.boots.dsp.messages.base.entity;

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

@Data
@ToString(exclude="tote")
@Entity
@JsonInclude(Include.NON_DEFAULT)
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@JsonIgnore
	@OneToOne
    private Tote tote;

	//representation of a tote orderlist
	//some fields are provided by 12N messages
	//some are provided by 32R messages
	//we want a combination of both - we don't want to have to copy between representations
	//the serialization control for 12N and 32R will provide us with the separation we need
	//allowing us to use a single class to represent the tote state
	//common fields
	private int numberOfOrderLines;
	private int orderLineReferenceNumberLength;
	private int orderLineTypeLength;
	private int pharmacyIdLength;
	private int patientIdLength;
	private int prescriptionIdLength;
	private int productIdLength;
	private int numPacksLength;
	private int numPillsLength;
	private int refOrderIdLength;
	private int refSheetNumLength;
// 12N fields
	private int packsPickedLength;
// 32R fields
	private int plasticBagIdLength;
	private int productBarcodeLength;
	private int operatorIdLength;
	private int roleIdLength;
	private int timestampLength;
	private int statusLength;
	
	@OneToMany(mappedBy="orderDetail",cascade={CascadeType.ALL})
	private List<OrderLine> orderLines;

	public OrderDetail() {
		orderLines = new ArrayList<OrderLine>();
	}
}
