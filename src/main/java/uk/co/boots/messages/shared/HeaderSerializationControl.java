package uk.co.boots.messages.shared;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Getter
public class HeaderSerializationControl {
	public final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 1, 3);
	public final SerialisationControlField orderIdInfo = new SerialisationControlField ("lengthOfOrderId", identifierInfo.getNextOffset(), 2);
	public final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("lengthOfOrderId", orderIdInfo.getNextOffset(), 2);
	public final int headerDataOffset = sheetNumberInfo.getNextOffset(); 
	private final SerialisationControlField[] serialisationInfo = {identifierInfo,orderIdInfo,sheetNumberInfo};
}
