package uk.co.boots.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Data
public class OperatorArrayListSerializationControl {
	private final SerialisationControlField operatorIdInfo;
	private final SerialisationControlField roleIdInfo;
	private final SerialisationControlField timestampInfo;
	
	public OperatorArrayListSerializationControl (OrderLineArrayListSerializationControl lineControl) {
		operatorIdInfo = new SerialisationControlField ("%02d", lineControl.getProductBarcodeInfo().getNextOffset(), 2);
		roleIdInfo = new SerialisationControlField ("%02d", operatorIdInfo.getNextOffset(), 2);
		timestampInfo = new SerialisationControlField ("%02d", roleIdInfo.getNextOffset(), 2);
	}
}
