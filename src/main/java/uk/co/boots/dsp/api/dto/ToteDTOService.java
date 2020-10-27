package uk.co.boots.dsp.api.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.messages.base.entity.Header;
import uk.co.boots.dsp.messages.base.entity.RawMessage;
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
		String searchTerm = pageRequestDetail.getSearchTerm();
		Page<Tote> totes;
		if (searchTerm != null && searchTerm.length() > 0) {
			totes = toteService.getTotePageUsingSearch(pageRequestDetail.getPageNumber(), pageRequestDetail.getPageSize(), searchTerm);
		} else {
			totes = toteService.getTotePage(pageRequestDetail.getPageNumber(), pageRequestDetail.getPageSize());
		}
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
	
	public ToteMessageSummary getMessageDTOsForTote(long toteId) {
		ToteMessageSummary tms = new ToteMessageSummary();
		tms.setToteId(toteId);
		List<RawMessageDTO> rml = tms.getMessages();
		List<RawMessage> messages = toteService.getRawMessagesByToteId(toteId);
		messages.forEach(message -> rml.add(convertRawMessageToDTO(message)));
		return tms;
	}
	
	private RawMessageDTO convertRawMessageToDTO(RawMessage msg) {
		RawMessageDTO dto = new RawMessageDTO();
		dto.setId(msg.getId());
		dto.setMessage(msg.getMessage());
		dto.setMessageType(msg.getMessageType());
		dto.setCreationTime(new SimpleDateFormat("dd.mm.yy HH.mm.ss").format(msg.getCreationDateTime()));
		return dto;
	}
}
