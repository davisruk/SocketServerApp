package uk.co.boots.dsp.api.dto;

import lombok.Data;

@Data
public class ToteSummaryDTO {
	private Long id;
	private String toteType;
	private String orderId;
	private String sheetNumber;
	private String containerIdentifier;
}
