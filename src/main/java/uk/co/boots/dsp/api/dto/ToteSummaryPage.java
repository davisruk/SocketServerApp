package uk.co.boots.dsp.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class ToteSummaryPage {
	private List<ToteSummaryDTO> toteEntries;
	private PageDetail pageDetail;
}
