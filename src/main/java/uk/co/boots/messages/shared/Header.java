package uk.co.boots.messages.shared;

import lombok.Data;
import uk.co.boots.messages.SerialisationControlField;

@Data
//@Embeddable
public class Header {
	public static final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 1, 3);
	public static final SerialisationControlField orderIdInfo = new SerialisationControlField ("lengthOfOrderId", identifierInfo.getNextOffset(), 2);
	public static final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("lengthOfOrderId", orderIdInfo.getNextOffset(), 2);
	public static final int headerDataOffset = sheetNumberInfo.getNextOffset(); 


	public final static String identifier = "12N";
	private int orderIdLength;
	private int sheetNumberLength;
	private String orderId;
	private String sheetNumber;
}
