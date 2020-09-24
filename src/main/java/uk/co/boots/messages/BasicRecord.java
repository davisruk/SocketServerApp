package uk.co.boots.messages;

import lombok.Data;

@Data
public abstract class BasicRecord {
	public static final SerialisationControlField identifierInfo = new SerialisationControlField("identifier", 0, 1);
	public static final SerialisationControlField fieldLengthInfo = new SerialisationControlField("fieldLength",
			identifierInfo.getNextOffset(), 2);

	private char identifier;
	private int payloadLength;
	private String payload;

	public int getPayloadDataOffset() {
		return fieldLengthInfo.getNextOffset();
	}

	public int getNextRecordOffset() {
		return getPayloadDataOffset() + getPayloadLength();
	}
}
