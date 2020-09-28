package uk.co.boots.messages;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.messages.shared.Tote;

@Data
@ToString(exclude="tote")
@MappedSuperclass
public abstract class BasicRecord {
	@Transient
	public static final SerialisationControlField identifierInfo = new SerialisationControlField("%c", 0, 1);
	@Transient
	public static final SerialisationControlField fieldLengthInfo = new SerialisationControlField("%02d",
			identifierInfo.getNextOffset(), 2);

	@Id
	@Column(name = "id")
    private Long id;
   
	@OneToOne
    @MapsId
    private Tote tote;
    
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
