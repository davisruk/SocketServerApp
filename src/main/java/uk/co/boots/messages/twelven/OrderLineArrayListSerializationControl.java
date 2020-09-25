package uk.co.boots.messages.twelven;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Getter
public class OrderLineArrayListSerializationControl {
	private final char identifier = 'X';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 0, 1);
	private final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("numberOfOrderLines", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("lengthOfOrderLineRef", numberOrderLinesInfo.getNextOffset(), 2);
	private final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("lengthOfOrderLineType", orderLineRefInfo.getNextOffset(), 2);	
	private final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("lengthOfPharmacyId", orderLineTypeInfo.getNextOffset(), 2);
	private final SerialisationControlField patientIdInfo = new SerialisationControlField ("lengthOfPatientId", pharmacyIdInfo.getNextOffset(), 2);
	private final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("lengthOfPrescriptionId", patientIdInfo.getNextOffset(), 2);
	private final SerialisationControlField productIdInfo = new SerialisationControlField ("lengthOfProductId", prescriptionIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPacksInfo = new SerialisationControlField ("lengthOfNumPacks", productIdInfo.getNextOffset(), 2);
	private final SerialisationControlField packsPickedInfo = new SerialisationControlField ("lengthOfPacksPicked", numPacksInfo.getNextOffset(), 2);
	private final SerialisationControlField numPillsInfo = new SerialisationControlField ("lengthOfNumPills", packsPickedInfo.getNextOffset(), 2);
	private final SerialisationControlField refOrderIdInfo = new SerialisationControlField ("lengthOfRefOrderId", numPillsInfo.getNextOffset(), 2);
	private final SerialisationControlField refSheetNumInfo = new SerialisationControlField ("lengthOfRefSheetNum", refOrderIdInfo.getNextOffset(), 2);
	
	private final int orderLineDataOffset = refSheetNumInfo.getNextOffset();

}
