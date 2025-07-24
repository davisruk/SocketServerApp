package uk.co.boots.dsp.messages.framework.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import uk.co.boots.dsp.messages.framework.serialization.SerialisationControlField;

@Data
@ToString(exclude="tote") //to stop circular dependency with Tote blowing stack on toString call
@MappedSuperclass
@JsonInclude(Include.NON_NULL)
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
	@JsonIgnore
	public int getPayloadDataOffset() {
		return fieldLengthInfo.getNextOffset();
	}

	@Transient
	@JsonIgnore
	public int getNextRecordOffset() {
		return getPayloadDataOffset() + getPayloadLength();
	}
}
