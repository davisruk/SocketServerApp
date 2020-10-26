package uk.co.boots.dsp.api.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.messages.base.entity.Header;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.messages.base.entity.TransportContainer;
import uk.co.boots.dsp.wcs.service.ToteService;

@Service
public class ToteDTOService {
	@Autowired
	private ToteService toteService;
	
	public ToteSummaryDTO convertToteEntityToToteSummaryDTO(Tote tote) {
		ToteSummaryDTO dto = new ToteSummaryDTO();
		dto.setId(tote.getId());
		TransportContainer tc = tote.getTransportContainer();
		if (tc != null) {
			dto.setContainerIdentifier(tc.getPayload());	
		} else {
			dto.setContainerIdentifier("None");
		}
		Header h = tote.getHeader();
		dto.setSheetNumber(h.getSheetNumber());
		dto.setToteType(tote.getToteIdentifier().getPayload());
		dto.setOrderId(h.getOrderId());
		return dto;
	}
	
	public ToteSummaryPage getSummaryDTOsForPage(PageRequestDetail pageRequestDetail){
		ToteSummaryPage result = new ToteSummaryPage();
		PageDetail pd = new PageDetail();
		pd.setPageRequestDetail(pageRequestDetail);
		Page<Tote>totes = toteService.getTotePage(pageRequestDetail.getPageNumber(), pageRequestDetail.getPageSize());
		List<ToteSummaryDTO> toteSummaryList = new ArrayList<ToteSummaryDTO>();
		totes.forEach(tote -> toteSummaryList.add(convertToteEntityToToteSummaryDTO(tote)));
		result.setToteEntries(toteSummaryList);
		PageResponseDetail prd = new PageResponseDetail();
		prd.setTotalEntries((int)totes.getTotalElements());
		prd.setTotalPages(totes.getTotalPages());
		pd.setPageResponseDetail(prd);
		result.setPageDetail(pd);
		return result;
	}
}
