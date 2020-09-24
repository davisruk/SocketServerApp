package uk.co.boots.messages.twelven;

import java.util.ArrayList;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.SerialisationControlField;

@Data
@ToString(callSuper = true)
public class OrderLineArrayList extends ArrayList<OrderLine> {
	private static final char identifier = 'X';

	public static final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 0, 1);
	public static final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("numberOfOrderLines", identifierInfo.getNextOffset(), 3);
	public static final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("lengthOfOrderLineRef", numberOrderLinesInfo.getNextOffset(), 2);
	public static final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("lengthOfOrderLineType", orderLineRefInfo.getNextOffset(), 2);	
	public static final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("lengthOfPharmacyId", orderLineTypeInfo.getNextOffset(), 2);
	public static final SerialisationControlField patientIdInfo = new SerialisationControlField ("lengthOfPatientId", pharmacyIdInfo.getNextOffset(), 2);
	public static final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("lengthOfPrescriptionId", patientIdInfo.getNextOffset(), 2);
	public static final SerialisationControlField productIdInfo = new SerialisationControlField ("lengthOfProductId", prescriptionIdInfo.getNextOffset(), 2);
	public static final SerialisationControlField numPacksInfo = new SerialisationControlField ("lengthOfNumPacks", productIdInfo.getNextOffset(), 2);
	public static final SerialisationControlField packsPickedInfo = new SerialisationControlField ("lengthOfPacksPicked", numPacksInfo.getNextOffset(), 2);
	public static final SerialisationControlField numPillsInfo = new SerialisationControlField ("lengthOfNumPills", packsPickedInfo.getNextOffset(), 2);
	public static final SerialisationControlField refOrderIdInfo = new SerialisationControlField ("lengthOfRefOrderId", numPillsInfo.getNextOffset(), 2);
	public static final SerialisationControlField refSheetNumInfo = new SerialisationControlField ("lengthOfRefSheetNum", refOrderIdInfo.getNextOffset(), 2);
	
	public static final int orderLineDataOffset = refSheetNumInfo.getNextOffset();

	
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
