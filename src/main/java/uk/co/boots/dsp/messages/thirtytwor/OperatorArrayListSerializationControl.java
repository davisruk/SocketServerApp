package uk.co.boots.dsp.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Data;
import uk.co.boots.dsp.messages.SerialisationControlField;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;

@Component
@Data
public class OperatorArrayListSerializationControl {
	private final SerialisationControlField numberOperatorLinesInfo = new SerialisationControlField ("%02d", 0, 2);
	
	public int getOperatorIdOffset(OrderDetail al) {
		// we don't count numberOfOoperatorLines
		return 0;
	}

	public int getRoleIdOffset(OrderDetail al) {
		return getOperatorIdOffset(al) + al.getOperatorIdLength();
	}
	
	public int getTimestampOffset(OrderDetail al) {
		return getRoleIdOffset(al) + al.getRoleIdLength();
	}
	
	public int getNextLineOffset(OrderDetail al) {
		return getTimestampOffset(al) + al.getTimestampLength();
	}
}
