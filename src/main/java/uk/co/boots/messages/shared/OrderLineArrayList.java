package uk.co.boots.messages.shared;

import java.util.ArrayList;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class OrderLineArrayList extends ArrayList<OrderLine> {
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
	private int roleIdLenght;
	private int timestampLength;
	private int statusLength;
}
