package uk.co.boots.messages.shared;

import lombok.Data;
import uk.co.boots.messages.thirtytwor.OperatorArrayList;

@Data
public class OrderLine {
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
	private OperatorArrayList operators;
	private String status;
}
