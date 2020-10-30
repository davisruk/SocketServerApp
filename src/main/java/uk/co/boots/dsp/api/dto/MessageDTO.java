package uk.co.boots.dsp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.boots.dsp.messages.base.entity.Tote;
@Data
@AllArgsConstructor
public class MessageDTO {
	private String rawMessage;
	private Tote message;
}
