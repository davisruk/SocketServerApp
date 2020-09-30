package uk.co.boots.messages.persistence;

import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import uk.co.boots.messages.shared.OrderDetail;
import uk.co.boots.messages.shared.OrderLine;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.thirtytwor.EndTime;
import uk.co.boots.messages.thirtytwor.OperatorDetail;
import uk.co.boots.messages.thirtytwor.OperatorLine;
import uk.co.boots.messages.thirtytwor.StartTime;
import uk.co.boots.messages.thirtytwor.Status;
import uk.co.boots.messages.thirtytwor.ToteStatusDetail;


@Service
public class ToteService {
	@Autowired
	ToteRepository toteRepository;
	
	public Page<Tote> getTotePage(int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Order.asc("id")));
		return toteRepository.findAll(pageable);
	}
	
	public Tote save(Tote tote) {
		return toteRepository.save(tote);
	}
	
	public Tote setupStartTime (Calendar time, Tote t) {
		StartTime st = new StartTime();
		st.setPayload(convertTime(time, "%02d%02d%02d"));
		t.setStartTime(st);
		t.setStartCal(time);
		return t;
	}
	
	public Tote setupEndTime (Calendar time, Tote t) {
		EndTime et = new EndTime();
		et.setPayload(convertTime(time, "%02d%02d%02d"));
		t.setEndTime(et);
		t.setEndCal(time);
		return t;
	}
	
	public ToteStatusDetail setupToteStatus(Tote t, String status) {
		ToteStatusDetail tsd = new ToteStatusDetail();
		tsd.setNumberOfLines(1);
		tsd.setStatusLength(4);
		Status s = new Status();
		s.setStatus("0030");
		s.setStatusDetail(tsd);
		tsd.getStatusList().add(s);
		t.setStatusDetail(tsd);
		return tsd;
	}
	
	public void setupOperators(Tote t) {
		OrderDetail od = t.getOrderDetail();
		if (od != null) {
			List<OrderLine> ol = od.getOrderLines(); 
			ol.forEach(line -> {
				OperatorDetail opd = new OperatorDetail();
				opd.setNumberOfLines(1);
				OperatorLine opl = new OperatorLine();
				opl.setOperatorId("RDavis  ");
				opl.setRoleId("Solution Architect  ");
				opd.addOperatorLine(opl);
				line.setOperatorDetail(opd);
				Calendar opc = Calendar.getInstance();
				opc.setTimeInMillis(t.getStartCal().getTimeInMillis() - t.getEndCal().getTimeInMillis() / 2);
				opl.setTimestamp(convertDate(opc) + " " + convertTime(opc, "%02d.%02d.%02d"));
			});
		}
	}
	
	private String convertDate(Calendar c) {
		return String.format("%02d.%02d.%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
				c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH));
	}

	private String convertTime(Calendar c, String format) {
		return String.format(format, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}
	
}
