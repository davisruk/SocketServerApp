package uk.co.boots.messages.twelven;

import lombok.Data;

@Data
public class OrderLine {
	private String orderLineNumber;
	private String orderLineType;
	private String pharmacyId;
	private String patientId;
	private String prescriptionId;
	private String productId;
	private String numberOfPacks;
	private String numberOfPacksPicked;
	private String numberOfPills;
	private String referenceOrderId;
	private String referenceSheetNumber;

}
