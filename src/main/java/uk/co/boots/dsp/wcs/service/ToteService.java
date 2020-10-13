package uk.co.boots.dsp.wcs.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import uk.co.boots.dsp.comms.DSPCommsMessage;
import uk.co.boots.dsp.messages.Serializer;
import uk.co.boots.dsp.messages.SerializerFactory;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.shared.RawMessage;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.messages.shared.ToteIdentifier;
import uk.co.boots.dsp.messages.thirtytwor.EndTime;
import uk.co.boots.dsp.messages.thirtytwor.GsOneDetail;
import uk.co.boots.dsp.messages.thirtytwor.GsOneLine;
import uk.co.boots.dsp.messages.thirtytwor.OperatorDetail;
import uk.co.boots.dsp.messages.thirtytwor.OperatorLine;
import uk.co.boots.dsp.messages.thirtytwor.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.messages.thirtytwor.StartTime;
import uk.co.boots.dsp.messages.thirtytwor.Status;
import uk.co.boots.dsp.messages.thirtytwor.ToteStatusDetail;
import uk.co.boots.dsp.wcs.repository.ToteRepository;
import uk.co.boots.dsp.wcs.rules.RuleParameters;
import uk.co.boots.dsp.wcs.rules.RuleProcessorFactory;

@Service
public class ToteService {
	@Autowired
	ToteRepository toteRepository;
	@Autowired
	private SerializerFactory serializerFactory;
	@Autowired
	private MasterDataService masterDataService;
	@Autowired
	private RuleProcessorFactory ruleProcessorFactory;
	
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
	
	public void deleteAll() {
		toteRepository.deleteAll();
	}

	public long getToteCount() {
		return toteRepository.count();
	}

	public Tote setupStartTime(Calendar time, Tote t) {
		StartTime st = new StartTime();
		st.setPayload(convertTime(time, "HHmmss"));
		t.setStartTime(st);
		t.setStartCal(time);
		return t;
	}

	public Tote setupEndTime(Calendar time, Tote t) {
		EndTime et = new EndTime();
		et.setPayload(convertTime(time, "HHmmss"));
		t.setEndTime(et);
		t.setEndCal(time);
		return t;
	}

	public ToteStatusDetail addNewToteStatus(Tote t, String status) {
		ToteStatusDetail tsd = t.getStatusDetail();
		if (tsd == null) {
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

	public void setupOrderLines(Tote tote) {
		OrderDetail od = tote.getOrderDetail();
		if (od != null) {
			List<OrderLine> orderLines = od.getOrderLines();
			
			IntStream.range(0, orderLines.size()).forEach(idx -> {
				OrderLine ol = orderLines.get(idx);
				ol.setPlasticBagId("12345678");
				setupBarcode(ol);
				setupOperators(ol);
				setupGSOne(ol);
				setupPickedValues (ol, tote.getToteIdentifier().getPayload(), idx);
			});
		}
	}
	
	private OrderLine findOrderLineInTote(Tote tote, String orderLineNumber) {
		return tote.getOrderDetail().getOrderLines()
			.stream()
			.filter(line -> line.getOrderLineNumber().equals(orderLineNumber))
			.findAny()
			.orElse(null);
	}

	public void setupPickedValues(OrderLine orderLine, String toteIdentifier, int orderLineIndex) {
		if (ToteIdentifier.EMPTY_TOTE.equals(toteIdentifier) || ToteIdentifier.ASSOCIATED_TOTE.equals(toteIdentifier)){
			Tote t = getRelatedToteForOrder(orderLine.getOrderLineNumber(), ToteIdentifier.ADAPTED_TOTE);
			if (t != null) {
				OrderLine relatedLine = findOrderLineInTote(t, orderLine.getOrderLineNumber());
				// finish this off tomorrow
				if (relatedLine != null) {
					orderLine.setNumberOfPills(relatedLine.getNumberOfPills());
				}
			} else {
				t = getRelatedToteForOrder(orderLine.getOrderLineNumber(), ToteIdentifier.MANUAL_TOTE);
				if (t != null) {
					OrderLine relatedLine = findOrderLineInTote(t, orderLine.getOrderLineNumber());
					orderLine.setNumberOfPacks(relatedLine.getNumberOfPacks());
				}
			}
		}
		// check the rules data to see if this line should be changed
		masterDataService.getRulesForProduct(orderLine.getProductId()).ifPresent(l -> processRules(l, orderLine, orderLineIndex));
	}

	public void setupOperators(OrderLine orderLine) {
		OperatorDetail opd = new OperatorDetail();
		opd.setNumberOfLines(1);
		opd.setOrderLine(orderLine);
		OperatorLine opl = new OperatorLine();
		opl.setOperatorId(OrderLineArrayListSerializationControl.formatOperatorId("RDavis"));
		opl.setRoleId(OrderLineArrayListSerializationControl.formatRoleId("Solution Architect"));
		opd.addOperatorLine(opl);
		orderLine.setOperatorDetail(opd);
		opl.setTimestamp(OrderLineArrayListSerializationControl.formatTimeStamp(Calendar.getInstance().getTime()));
	}

	public void setupGSOne(OrderLine orderLine) {
		char FNC = '\u001D';
		GsOneDetail gsod = new GsOneDetail();
		gsod.setNumberOfLines(0);
		masterDataService.getInfoForProduct(orderLine.getProductId()).ifPresent(product -> {
				String gtin = product.getGtin();
				if (gtin != null && gtin.length() > 0) {
					gsod.setNumberOfLines(1);
					GsOneLine line = new GsOneLine();
					if (orderLine.getOrderLineType().equals(ToteIdentifier.ADAPTED_TOTE)) {
						line.setSplitIndicator('1');
					} else {
						line.setSplitIndicator('0');
					}
					String gsOne = "01" + gtin + "21" + "SERIALNUMBER01234567" + FNC + "10ABATCHCODE12345" + FNC + "17191201";
					line.setGsOne(gsOne);
					line.setLengthOfGSone(String.format("%2d", gsOne.length()));
					gsod.addGsOneLine(line);
					line.setGsOneDetail(gsod);
					gsod.setOrderLine(orderLine);
				}
			});
		orderLine.setGsOneDetail(gsod);		
	}

	public DSPCommsMessage processToteFinished(Tote tote) {
		// tote has travelled track, send back 32R Long
		Serializer s = serializerFactory.getSerializer("32RLong").get();
		Date now = new Date();
		// change tote status to complete
		addNewToteStatus(tote, "0004");
		RawMessage rm = new RawMessage(s.getType(), new String(s.serialize(tote)), now);
		return new DSPCommsMessage(rm, s.getResponseProcessor(tote), tote);
	}

	private String convertTime(Calendar c, String format) {
		return new SimpleDateFormat(format).format(c.getTime());
	}

	public DSPCommsMessage processClientOrderPersisted(Tote tote) {
		// TODO Auto-generated method stub
		Serializer s = serializerFactory.getSerializer("32RShort").get();
		Date now = new Date();
		// set tote status to order started
		addNewToteStatus(tote, "0001");
		setupStartTime(Calendar.getInstance(), tote);
		RawMessage rm = new RawMessage(s.getType(), new String(s.serialize(tote)), now);
		return new DSPCommsMessage(rm, s.getResponseProcessor(tote), tote);
	}

	public Tote getRelatedToteForOrder(String orderReference, String toteOrderType) {
		List<Tote> tl = toteRepository.findRelatedToteForOrderLine(orderReference, toteOrderType);
		if (tl == null || tl.size() == 0)
			return null;
		return tl.get(0);
	}
	
	public OrderLine getRelatedOrderLineForOrderLineNumber(String orderLineReference, String toteOrderType) {
		List<OrderLine> olList = toteRepository.findRelatedOrderLineByOrderLineNumber(orderLineReference, toteOrderType);
		if (olList == null || olList.size() == 0)
			return null;
		return olList.get(0);
	}
	
	private void setupBarcode(OrderLine line) {
		OrderDetail od = line.getOrderDetail();
		od.setProductBarcodeLength(OrderLineArrayListSerializationControl.BARCODE_DATA_LENGTH);
		masterDataService.getInfoForProduct(line.getProductId()).ifPresentOrElse(product ->
										line.setProductBarcode(OrderLineArrayListSerializationControl.formatProductBarcode(product.getEanBarcode())),
									() -> 
										line.setProductBarcode(OrderLineArrayListSerializationControl.formatProductBarcode("No Barcode"))
									);
	}

	private void processRules(List<RuleParameters> rules, OrderLine line, int orderLineIndex) {
		rules.forEach(rule -> {
			ruleProcessorFactory.getProcessor(rule.getRuleType()).ifPresent(rp -> {
				rp.process(line, orderLineIndex);
			});
		});
	}
}
