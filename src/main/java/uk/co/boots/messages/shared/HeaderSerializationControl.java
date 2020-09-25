package uk.co.boots.messages.shared;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Getter
public class HeaderSerializationControl {
	private final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 1, 3);
	private final SerialisationControlField orderIdInfo = new SerialisationControlField ("lengthOfOrderId", identifierInfo.getNextOffset(), 2);
	private final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("lengthOfOrderId", orderIdInfo.getNextOffset(), 2);
	private final int headerDataOffset = sheetNumberInfo.getNextOffset(); 
	
	public int getMessageDataOffset() {
		return headerDataOffset;
	}

	public int getSheetNumberDataOffset(Header h) {
		// cannot call this unless orderId has been set
		return getMessageDataOffset() + h.getOrderIdLength();
	}
	
	public int getNextDataOffset(Header h) {
		return getSheetNumberDataOffset(h) + h.getSheetNumberLength(); 
	}
	
}
