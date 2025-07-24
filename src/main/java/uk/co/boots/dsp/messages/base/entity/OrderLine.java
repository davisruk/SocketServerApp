package uk.co.boots.dsp.messages.base.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.thirtytwor.entity.GsOneDetail;
import uk.co.boots.dsp.messages.thirtytwor.entity.OperatorDetail;

@Data
@ToString(exclude="orderDetail")
@Entity
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "orderLineNumber", "orderLineType", "pharmacyId",
	"patientId", "prescriptionId", "productId", "numberOfPacks",
	"numberOfPills", "referenceSheetNumber", "numberOfPacksPicked",
	"referenceOrderId", "plasticBagId", "productBarcode", "gsOneDetail",
	"operatorDetail", "status"})
public class OrderLine {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@JsonIgnore
	@ManyToOne (cascade={CascadeType.ALL})
	@JoinColumn (name="order_detail_id")
	private OrderDetail orderDetail;
	
	private String orderLineNumber;
	private String orderLineType;
	private String pharmacyId;
	private String patientId;
	private String prescriptionId;
	private String productId;
	private String numberOfPacks;
	private String numberOfPills;
	private String referenceSheetNumber;
	
	//12N
	private String numberOfPacksPicked;
	private String referenceOrderId;
	
	//32R
	private String plasticBagId;
	private String productBarcode;

	@OneToOne(cascade={CascadeType.ALL})
	private GsOneDetail gsOneDetail;

	@OneToOne(cascade={CascadeType.ALL})
	private OperatorDetail operatorDetail;
	private String status;
	
	public void setOperatorDetail (OperatorDetail od) {
		operatorDetail = od;
		od.setOrderLine(this);
	}
}
