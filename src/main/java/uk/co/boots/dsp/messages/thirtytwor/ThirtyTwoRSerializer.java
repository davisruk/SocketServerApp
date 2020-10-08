package uk.co.boots.dsp.messages.thirtytwor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.BasicMessage;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.Serializer;
import uk.co.boots.dsp.messages.shared.Header;
import uk.co.boots.dsp.messages.shared.HeaderSerializationControl;
import uk.co.boots.dsp.messages.shared.OrderDetail;
import uk.co.boots.dsp.messages.shared.OrderLine;
import uk.co.boots.dsp.messages.shared.Tote;
import uk.co.boots.dsp.messages.shared.ToteIdentifier;
import uk.co.boots.dsp.messages.shared.TransportContainer;
import uk.co.boots.dsp.wcs.OSRBuffer;

@Component
public class ThirtyTwoRSerializer implements Serializer {

	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private StatusArrayListSerializationControl statusArrayListSerializationControl;
	@Autowired
	private OrderLineArrayListSerializationControl orderLineArrayListSerializationControl;
	@Autowired
	private OperatorArrayListSerializationControl operatorArrayListSerializationControl;
	@Autowired
	private GsOneArrayListSerializationControl gsOneArrayListSerializationControl;
	@Autowired
	private OSRBuffer osrBuffer;
	
	@Override
	public boolean canHandle(String messageType) {
		return "32RLong".equals(messageType);
	}

	@Override
	public byte[] serialize(BasicMessage msg) {
		// TODO Auto-generated method stub
		Tote t = (Tote) msg;
		StringBuffer sb = new StringBuffer();
		sb.append("32R");
		sb = processHeader(t.getHeader(), sb, headerSerializationControl);

		ToteIdentifier ti = t.getToteIdentifier();
		if (ti != null) {
			sb = processBasicRecord(ti, sb);
		}

		TransportContainer tc = t.getTransportContainer();
		if (tc != null) {
			sb = processBasicRecord(tc, sb);
		}

		sb = processBasicRecord(t.getStartTime(), sb);
		sb = processBasicRecord(t.getEndTime(), sb);
		sb = processStatus(t.getStatusDetail(), sb, statusArrayListSerializationControl);
		sb = processOrderDetail(t.getOrderDetail(), sb, orderLineArrayListSerializationControl);

		// have to process this part of the header here because we don't know the size
		// of the message until now
		int messageLength = sb.length() + headerSerializationControl.getSizeInfo().getSize();
		sb.insert(0, String.format(headerSerializationControl.getSizeInfo().getFormat(), messageLength));
		return sb.toString().getBytes();
	}

	private StringBuffer processHeader(Header h, StringBuffer sb, HeaderSerializationControl sc) {
		sb.append(String.format(sc.getOrderIdInfo().getFormat(), h.getOrderIdLength()));
		sb.append(String.format(sc.getSheetNumberInfo().getFormat(), h.getSheetNumberLength()));
		sb.append(h.getOrderId());
		sb.append(h.getSheetNumber());
		return sb;
	}

	private StringBuffer processBasicRecord(BasicRecord br, StringBuffer sb) {
		if (br == null) return sb;
		br.setPayloadLength(br.getPayload().length());
		sb.append(br.getIdentifier());
		sb.append(String.format(BasicRecord.fieldLengthInfo.getFormat(), br.getPayloadLength()));
		sb.append(br.getPayload());
		return sb;

	}

	private StringBuffer processStatus(ToteStatusDetail sal, StringBuffer sb, StatusArrayListSerializationControl sc) {
		if (sal == null)
			return sb;
		sb.append(sc.getIdentifier());
		sb.append(String.format(sc.getNumberOfEntries().getFormat(), sal.getNumberOfLines()));
		sb.append(String.format(sc.getStatusLength().getFormat(), sal.getStatusLength()));
		sal.getStatusList().forEach(status -> sb.append(status.getStatus()));
		return sb;
	}

	private StringBuffer processOrderDetail(OrderDetail od, StringBuffer sb,
			OrderLineArrayListSerializationControl sc) {
		OperatorArrayListSerializationControl oc = sc.getOperatorArrayListSerializationControl();

		if (od == null)
			return sb;
		// Refactor - These are not set in the 12N, should really get this info from the
		// serialization controller
		od.setPlasticBagIdLength(8);
		od.setProductBarcodeLength(13);
		od.setTimestampLength(17);
		od.setRoleIdLength(20);
		od.setOperatorIdLength(8);
		od.setStatusLength(2);
		// end of refactor

		sb.append(orderLineArrayListSerializationControl.getIdentifier());
		sb.append(String.format(sc.getNumberOrderLinesInfo().getFormat(), od.getNumberOfOrderLines()));
		sb.append(String.format(sc.getOrderLineRefInfo().getFormat(), od.getOrderLineReferenceNumberLength()));
		sb.append(String.format(sc.getOrderLineTypeInfo().getFormat(), od.getOrderLineTypeLength()));
		sb.append(String.format(sc.getPharmacyIdInfo().getFormat(), od.getPharmacyIdLength()));
		sb.append(String.format(sc.getPatientIdInfo().getFormat(), od.getPatientIdLength()));
		sb.append(String.format(sc.getPrescriptionIdInfo().getFormat(), od.getPrescriptionIdLength()));
		sb.append(String.format(sc.getPlasticBagIdInfo().getFormat(), od.getPlasticBagIdLength()));
		sb.append(String.format(sc.getProductIdInfo().getFormat(), od.getProductIdLength()));
		sb.append(String.format(sc.getNumPacksInfo().getFormat(), od.getNumPacksLength()));
		sb.append(String.format(sc.getNumPillsInfo().getFormat(), od.getNumPillsLength()));
		sb.append(String.format(sc.getProductBarcodeInfo().getFormat(), od.getProductBarcodeLength()));
		sb.append(String.format(oc.getOperatorIdInfo().getFormat(), od.getOperatorIdLength()));
		sb.append(String.format(oc.getRoleIdInfo().getFormat(), od.getRoleIdLength()));
		sb.append(String.format(oc.getTimestampInfo().getFormat(), od.getTimestampLength()));
		sb.append(String.format(sc.getStatusInfo().getFormat(), od.getStatusLength()));

		List<OrderLine> ola = od.getOrderLines();
		if (ola == null || ola.size() == 0)
			return sb;
		ola.forEach(line -> processOrderLine(line, sb));
		return sb;
	}

	private void processOrderLine(OrderLine ol, StringBuffer sb) {
		ol.setPlasticBagId("12345678");
		ol.setProductBarcode("1234567890123");
		sb.append(ol.getOrderLineNumber());
		sb.append(ol.getOrderLineType());
		sb.append(ol.getPharmacyId());
		sb.append(ol.getPatientId());
		sb.append(ol.getPrescriptionId());
		sb.append(ol.getPlasticBagId());
		sb.append(ol.getProductId());
		sb.append(ol.getNumberOfPacks());
		sb.append(ol.getNumberOfPills());
		sb.append(ol.getProductBarcode());
		
		// FMD here
		if (osrBuffer.processingFMD())
			sb = processGsOne(ol.getGSOneDetail(), sb, gsOneArrayListSerializationControl);
		
		
		
		
		sb = processOperators(ol.getOperatorDetail(), sb, operatorArrayListSerializationControl);
		ol.setStatus("30");
		sb.append(ol.getStatus());
	}

	private StringBuffer processOperators(OperatorDetail od, StringBuffer sb,
			OperatorArrayListSerializationControl sc) {
		if (od == null)
			return sb;
		sb.append(String.format("%02d", od.getNumberOfLines()));
		List<OperatorLine> opl = od.getOperatorList();
		opl.forEach(line -> {
			sb.append(line.getOperatorId());
			sb.append(line.getRoleId());
			sb.append(line.getTimestamp());

		});
		return sb;
	}
	
	private StringBuffer processGsOne(GsOneDetail gsod, StringBuffer sb,
			GsOneArrayListSerializationControl sc) {
		if (gsod == null)
			return sb;
		sb.append(String.format("%02d", gsod.getNumberOfLines()));
		List<GsOneLine> l = gsod.getGsOneLines();
		l.forEach(line -> {
			sb.append(line.getLengthOfGSone());
			sb.append(line.getGsOne());
			sb.append(line.getSplitIndicator());
		});
		return sb;
	}
	

	@Override
	public String getType() {
		return "32R-Long";
	}

	@Override
	public MessageResponseHandler getResponseProcessor(BasicMessage message) {
		Tote t = (Tote) message;
		return new FortyTwoRProcessor(t);
	}
}
