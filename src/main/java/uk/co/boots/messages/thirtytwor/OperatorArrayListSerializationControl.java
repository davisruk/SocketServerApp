package uk.co.boots.messages.thirtytwor;

import lombok.Data;
import uk.co.boots.messages.SerialisationControlField;

@Data
public class OperatorArrayListSerializationControl {
	private final SerialisationControlField operatorIdInfo;
	private final SerialisationControlField roleIdInfo;
	private final SerialisationControlField timestampInfo;
	
	public OperatorArrayListSerializationControl (OrderLineArrayListSerializationControl lineControl) {
		operatorIdInfo = new SerialisationControlField ("lengthOfOperatorId", lineControl.getProductBarcodeInfo().getNextOffset(), 2);
		roleIdInfo = new SerialisationControlField ("lengthOfRoleId", operatorIdInfo.getNextOffset(), 2);
		timestampInfo = new SerialisationControlField ("lengthOfTimestamp", roleIdInfo.getNextOffset(), 2);
	}
}
