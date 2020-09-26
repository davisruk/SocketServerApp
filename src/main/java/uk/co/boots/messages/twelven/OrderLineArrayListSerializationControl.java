package uk.co.boots.messages.twelven;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;
import uk.co.boots.messages.shared.OrderLineArrayList;

@Component
@Getter
public class OrderLineArrayListSerializationControl {
	private final char identifier = 'X';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%c", 0, 1);
	private final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("%03d", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("%02d", numberOrderLinesInfo.getNextOffset(), 2);
	private final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("%02d", orderLineRefInfo.getNextOffset(), 2);	
	private final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("%02d", orderLineTypeInfo.getNextOffset(), 2);
	private final SerialisationControlField patientIdInfo = new SerialisationControlField ("%02d", pharmacyIdInfo.getNextOffset(), 2);
	private final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("%02d", patientIdInfo.getNextOffset(), 2);
	private final SerialisationControlField productIdInfo = new SerialisationControlField ("%02d", prescriptionIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPacksInfo = new SerialisationControlField ("%02d", productIdInfo.getNextOffset(), 2);
	private final SerialisationControlField packsPickedInfo = new SerialisationControlField ("%02d", numPacksInfo.getNextOffset(), 2);
	private final SerialisationControlField numPillsInfo = new SerialisationControlField ("%02d", packsPickedInfo.getNextOffset(), 2);
	private final SerialisationControlField refOrderIdInfo = new SerialisationControlField ("%02d", numPillsInfo.getNextOffset(), 2);
	private final SerialisationControlField refSheetNumInfo = new SerialisationControlField ("%02d", refOrderIdInfo.getNextOffset(), 2);
	
	private final int orderLineDataOffset = refSheetNumInfo.getNextOffset();

	public int getOrderLineReferenceNumberOffset(OrderLineArrayList al) {
		// we don't count numberOfOrderLines
		return 0;
	}
	
	public int getOrderLineTypeOffset(OrderLineArrayList al) {
		return getOrderLineReferenceNumberOffset(al) + al.getOrderLineReferenceNumberLength();
	}
	
	public int getPharmacyIdOffset(OrderLineArrayList al) {
		return getOrderLineTypeOffset(al) + al.getOrderLineTypeLength(); 
	}
	
	public int getPatientIdOffset(OrderLineArrayList al) {
		return getPharmacyIdOffset(al) + al.getPharmacyIdLength();
	}

	public int getPrescriptionIdOffset(OrderLineArrayList al) {
		return getPatientIdOffset(al) + al.getPatientIdLength();
	}
	
	public int getProductIdOffset(OrderLineArrayList al) {
		return getPrescriptionIdOffset(al) + al.getPrescriptionIdLength();
	}
	
	public int getNumPacksOffset(OrderLineArrayList al) {
		return getProductIdOffset(al) + al.getProductIdLength();
	}
	
	public int getPacksPickedOffset(OrderLineArrayList al) {
		return getNumPacksOffset(al) + al.getNumPacksLength();
	}
	
	public int getNumPillsOffset(OrderLineArrayList al) {
		return getPacksPickedOffset(al) + al.getPacksPickedLength();
	}
	
	public int getRefOrderIdOffset(OrderLineArrayList al) {
		return getNumPillsOffset(al) + al.getNumPillsLength();
	}
	
	public int getRefSheetNumOffset(OrderLineArrayList al) {
		return getRefOrderIdOffset(al) + al.getRefOrderIdLength();
	}
		
	public int getNextLineOffset(OrderLineArrayList al) {
		return getRefSheetNumOffset(al) + al.getRefSheetNumLength();
	}
	
}
