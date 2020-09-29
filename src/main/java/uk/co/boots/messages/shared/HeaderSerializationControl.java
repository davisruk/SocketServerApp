package uk.co.boots.messages.shared;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Getter
public class HeaderSerializationControl {
	private final SerialisationControlField sizeInfo = new SerialisationControlField ("%05d", 1, 5);
	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%s", sizeInfo.getNextOffset(), 3);
	private final SerialisationControlField orderIdInfo = new SerialisationControlField ("%02d", identifierInfo.getNextOffset(), 2);
	private final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("%02d", orderIdInfo.getNextOffset(), 2);
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
