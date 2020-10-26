package uk.co.boots.dsp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequestDetail {
	private int pageNumber;
	private int pageSize;
}
