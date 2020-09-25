package uk.co.boots.messages.twelven;

import lombok.Data;
import uk.co.boots.messages.SerialisationControlField;

@Data
public class Header {
	public static final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 1, 3);
	public static final SerialisationControlField orderIdInfo = new SerialisationControlField ("lengthOfOrderId", identifierInfo.getNextOffset(), 2);
	public static final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("lengthOfOrderId", orderIdInfo.getNextOffset(), 2);
	public static final int headerDataOffset = sheetNumberInfo.getNextOffset(); 
	private static final SerialisationControlField[] serialisationInfo = {identifierInfo,orderIdInfo,sheetNumberInfo};

	public final static String identifier = "12N";
	private int orderIdLength;
	private int sheetNumberLength;
	private String orderId;
	private String sheetNumber;
	
	
	public int getOrderIdDataOffset() {
		return headerDataOffset;
	}

	public int getSheetNumberDataOffset() {
		// cannot call this unless orderId has been set
		return getOrderIdDataOffset() + orderIdLength;
	}
	
	public int getMessageDataOffset() {
		return getSheetNumberDataOffset() + sheetNumberLength; 
	}
		
}
