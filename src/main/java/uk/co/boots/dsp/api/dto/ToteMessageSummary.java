package uk.co.boots.dsp.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ToteMessageSummary {
	private long toteId;
	private List<RawMessageDTO> messages = new ArrayList<RawMessageDTO>();
}
