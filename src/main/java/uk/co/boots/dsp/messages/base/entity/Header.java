package uk.co.boots.dsp.messages.base.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Data;
import uk.co.boots.dsp.messages.framework.serialization.SerialisationControlField;

@Data
@Embeddable
public class Header {
	@Transient
	public static final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 1, 3);
	@Transient
	public static final SerialisationControlField orderIdInfo = new SerialisationControlField ("lengthOfOrderId", identifierInfo.getNextOffset(), 2);
	@Transient
	public static final SerialisationControlField sheetNumberInfo = new SerialisationControlField ("lengthOfOrderId", orderIdInfo.getNextOffset(), 2);
	@Transient
	public static final int headerDataOffset = sheetNumberInfo.getNextOffset(); 

	@Transient
	public final static String identifier = "12N";
	private int orderIdLength;
	private int sheetNumberLength;
	private String orderId;
	private String sheetNumber;
}
