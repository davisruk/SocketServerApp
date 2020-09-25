package uk.co.boots.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.messages.SerialisationControlField;

@Component ("thirtyTwoRSerializationControl")
@Data
public class OrderLineArrayListSerializationControl {
	private final char identifier = 'Z';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 0, 1);
	private final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("numberOfOrderLines", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("lengthOfOrderLineRef", numberOrderLinesInfo.getNextOffset(), 2);
	private final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("lengthOfOrderLineType", orderLineRefInfo.getNextOffset(), 2);	
	private final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("lengthOfPharmacyId", orderLineTypeInfo.getNextOffset(), 2);
	private final SerialisationControlField patientIdInfo = new SerialisationControlField ("lengthOfPatientId", pharmacyIdInfo.getNextOffset(), 2);
	private final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("lengthOfPrescriptionId", patientIdInfo.getNextOffset(), 2);
	private final SerialisationControlField plasticBagIdInfo = new SerialisationControlField ("lengthOfPlasticBagId", prescriptionIdInfo.getNextOffset(), 2);
	private final SerialisationControlField productIdInfo = new SerialisationControlField ("lengthOfProductId", plasticBagIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPacksInfo = new SerialisationControlField ("lengthOfNumPacks", productIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPillsInfo = new SerialisationControlField ("lengthOfNumPills", numPacksInfo.getNextOffset(), 2);
	private final SerialisationControlField productBarcodeInfo = new SerialisationControlField ("lengthOfProductBarcode", numPillsInfo.getNextOffset(), 2);
	private final OperatorArrayListSerializationControl operatorArrayListSerializationControl = new OperatorArrayListSerializationControl(this);
	private final SerialisationControlField statusInfo = new SerialisationControlField ("lengthOfStatus", operatorArrayListSerializationControl.getRoleIdInfo().getNextOffset(), 2);

}
