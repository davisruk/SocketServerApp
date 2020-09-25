package uk.co.boots.messages.twelven;

import java.util.ArrayList;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.SerialisationControlField;

@Data
@ToString(callSuper = true)
public class OrderLineArrayList extends ArrayList<OrderLine> {

	private int numberOfOrderLines;
	private int orderLineReferenceNumberLength;
	private int orderLineTypeLength;
	private int pharmacyIdLength;
	private int patientIdLength;
	private int prescriptionIdLength;
	private int productIdLength;
	private int numPacksLength;
	private int packsPickedLength;
	private int numPillsLength;
	private int refOrderIdLength;
	private int refSheetNumLength;
	
	public int getOrderLineReferenceNumberOffset() {
		// we don't count numberOfOrderLines
		return 0;
	}
	
	public int getOrderLineTypeOffset() {
		return getOrderLineReferenceNumberOffset() + orderLineReferenceNumberLength;
	}
	
	public int getPharmacyIdOffset() {
		return getOrderLineTypeOffset() + orderLineTypeLength; 
	}
	
	public int getPatientIdOffset() {
		return getPharmacyIdOffset() + pharmacyIdLength;
	}

	public int getPrescriptionIdOffset() {
		return getPatientIdOffset() + patientIdLength;
	}
	
	public int getProductIdOffset() {
		return getPrescriptionIdOffset() + prescriptionIdLength;
	}
	
	public int getNumPacksOffset() {
		return getProductIdOffset() + productIdLength;
	}
	
	public int getPacksPickedOffset() {
		return getNumPacksOffset() + numPacksLength;
	}
	
	public int getNumPillsOffset() {
		return getPacksPickedOffset() + packsPickedLength;
	}
	
	public int getRefOrderIdOffset() {
		return getNumPillsOffset() + numPillsLength;
	}
	
	public int getRefSheetNumOffset() {
		return getRefOrderIdOffset() + refOrderIdLength;
	}
		
	public int getNextLineOffset() {
		return getRefSheetNumOffset() + refSheetNumLength;
	}

}
