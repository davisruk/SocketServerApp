package uk.co.boots.dsp.wcs.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.comms.DSPCommunicationNotifier;
import uk.co.boots.dsp.messages.Serializer;
import uk.co.boots.dsp.messages.SerializerFactory;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.shared.RawMessage;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.messages.thirtytwor.EndTime;
import uk.co.boots.dsp.messages.thirtytwor.OperatorDetail;
import uk.co.boots.dsp.messages.thirtytwor.OperatorLine;
import uk.co.boots.dsp.messages.thirtytwor.StartTime;
import uk.co.boots.dsp.messages.thirtytwor.Status;
import uk.co.boots.dsp.messages.thirtytwor.ToteStatusDetail;
import uk.co.boots.dsp.wcs.repository.ToteRepository;

@Service
public class ToteService {
	@Autowired
	ToteRepository toteRepository;
	@Autowired
	private SerializerFactory serializerFactory;

	public Page<Tote> getTotePage(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Order.asc("id")));
		return toteRepository.findAll(pageable);
	}

	// allows the Tote table to be queried like a queue
	public Tote getToteInQueuePosition(int queuePos) {
		Page<Tote> page = getTotePage(queuePos, 1);
		return page.getNumberOfElements() > 0 ? page.toList().get(0) : null; 
	}

	public Tote save(Tote tote) {
		return toteRepository.save(tote);
	}
	
	public long getToteCount() {
		return toteRepository.count();
	}

	public Tote setupStartTime(Calendar time, Tote t) {
		StartTime st = new StartTime();
		st.setPayload(convertTime(time, "%02d%02d%02d"));
		t.setStartTime(st);
		t.setStartCal(time);
		return t;
	}

	public Tote setupEndTime(Calendar time, Tote t) {
		EndTime et = new EndTime();
		et.setPayload(convertTime(time, "%02d%02d%02d"));
		t.setEndTime(et);
		t.setEndCal(time);
		return t;
	}

	public ToteStatusDetail addNewToteStatus (Tote t, String status) {
		ToteStatusDetail tsd = t.getStatusDetail();
		if (tsd == null) 
		{
			tsd = new ToteStatusDetail();
			t.setStatusDetail(tsd);
			tsd.setStatusLength(4);
		}
		Status s = new Status();
		s.setStatus(status);
		s.setStatusDetail(tsd);
		tsd.getStatusList().add(s);
		t.setStatusDetail(tsd);
		tsd.setNumberOfLines(tsd.getStatusList().size());
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

	public DSPCommsMessage processToteFinished(Tote tote) {
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		Date now = new Date();
		//change tote status to complete
		addNewToteStatus(tote, "0004");
		RawMessage rm = new RawMessage (s.getType(), new String(s.serialize(tote)),now);
		return new DSPCommsMessage(rm, s.getResponseProcessor(tote), tote);
	}

	private String convertDate(Calendar c) {
		return String.format("%02d.%02d.%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
				c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH));
	}

	private String convertTime(Calendar c, String format) {
		return String.format(format, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	public DSPCommsMessage processClientOrderPersisted(Tote tote) {
		// TODO Auto-generated method stub
		Serializer s = serializerFactory.getSerializer("32RShort").get();
		Date now = new Date();
		// set tote status to order started
		addNewToteStatus(tote, "0001");
		setupStartTime(Calendar.getInstance(), tote);
		RawMessage rm = new RawMessage (s.getType(), new String(s.serialize(tote)), now);
		return new DSPCommsMessage(rm, s.getResponseProcessor(tote), tote);
	}
}