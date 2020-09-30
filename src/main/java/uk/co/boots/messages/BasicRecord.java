package uk.co.boots.messages;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="tote") //to stop circular dependency with Tote blowing stack on toString call
@MappedSuperclass
public abstract class BasicRecord {
	@Transient
	public static final SerialisationControlField identifierInfo = new SerialisationControlField("%c", 0, 1);
	@Transient
	public static final SerialisationControlField fieldLengthInfo = new SerialisationControlField("%02d",
			identifierInfo.getNextOffset(), 2);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
    private Long id;
   
    private char identifier;
	private int payloadLength;
	private String payload;

	@Transient
	public int getPayloadDataOffset() {
		return fieldLengthInfo.getNextOffset();
	}

	@Transient
	public int getNextRecordOffset() {
		return getPayloadDataOffset() + getPayloadLength();
	}
}
