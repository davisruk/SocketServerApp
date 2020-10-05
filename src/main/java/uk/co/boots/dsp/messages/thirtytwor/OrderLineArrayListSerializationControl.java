package uk.co.boots.dsp.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.dsp.messages.SerialisationControlField;

@Component ("thirtyTwoRSerializationControl")
@Data
public class OrderLineArrayListSerializationControl {
	private final char identifier = 'Z';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%c", 0, 1);
	private final SerialisationControlField numberOrderLinesInfo = new SerialisationControlField ("%03d", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField orderLineRefInfo = new SerialisationControlField ("%02d", numberOrderLinesInfo.getNextOffset(), 2);
	private final SerialisationControlField orderLineTypeInfo = new SerialisationControlField ("%02d", orderLineRefInfo.getNextOffset(), 2);	
	private final SerialisationControlField pharmacyIdInfo = new SerialisationControlField ("%02d", orderLineTypeInfo.getNextOffset(), 2);
	private final SerialisationControlField patientIdInfo = new SerialisationControlField ("%02d", pharmacyIdInfo.getNextOffset(), 2);
	private final SerialisationControlField prescriptionIdInfo = new SerialisationControlField ("%02d", patientIdInfo.getNextOffset(), 2);
	private final SerialisationControlField plasticBagIdInfo = new SerialisationControlField ("%02d", prescriptionIdInfo.getNextOffset(), 2);
	private final SerialisationControlField productIdInfo = new SerialisationControlField ("%02d", plasticBagIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPacksInfo = new SerialisationControlField ("%02d", productIdInfo.getNextOffset(), 2);
	private final SerialisationControlField numPillsInfo = new SerialisationControlField ("%02d", numPacksInfo.getNextOffset(), 2);
	private final SerialisationControlField productBarcodeInfo = new SerialisationControlField ("%02d", numPillsInfo.getNextOffset(), 2);
	private final OperatorArrayListSerializationControl operatorArrayListSerializationControl = new OperatorArrayListSerializationControl(this);
	private final SerialisationControlField statusInfo = new SerialisationControlField ("%02d", operatorArrayListSerializationControl.getRoleIdInfo().getNextOffset(), 2);

}
