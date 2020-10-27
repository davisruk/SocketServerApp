package uk.co.boots.dsp.api.dto;

import lombok.Data;

@Data
public class RawMessageDTO {
	private long id;
	private String message;
	private String messageType;
	private String creationTime;
}
