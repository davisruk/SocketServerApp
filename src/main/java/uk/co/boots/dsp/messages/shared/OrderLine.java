package uk.co.boots.dsp.messages.shared;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.thirtytwor.OperatorDetail;

@Data
@ToString(exclude="orderDetail")
@Entity
public class OrderLine {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
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
	private OperatorDetail operatorDetail;
	private String status;
	
	public void setOperatorDetail (OperatorDetail od) {
		operatorDetail = od;
		od.setOrderLine(this);
	}
}