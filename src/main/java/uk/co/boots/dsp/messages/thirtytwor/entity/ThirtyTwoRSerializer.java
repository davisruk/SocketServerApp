package uk.co.boots.dsp.messages.thirtytwor.entity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.base.entity.Header;
import uk.co.boots.dsp.messages.base.entity.OrderDetail;
import uk.co.boots.dsp.messages.base.entity.OrderLine;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.messages.base.entity.ToteIdentifier;
import uk.co.boots.dsp.messages.base.entity.TransportContainer;
import uk.co.boots.dsp.messages.base.serialization.HeaderSerializationControl;
import uk.co.boots.dsp.messages.framework.entity.BasicMessage;
import uk.co.boots.dsp.messages.framework.entity.BasicRecord;
import uk.co.boots.dsp.messages.framework.serialization.SerializationControlIdentifiers;
import uk.co.boots.dsp.messages.framework.serialization.Serializer;
import uk.co.boots.dsp.messages.thirtytwor.FortyTwoRProcessor;
import uk.co.boots.dsp.messages.thirtytwor.serialization.GsOneArrayListSerializationControl;
import uk.co.boots.dsp.messages.thirtytwor.serialization.OperatorArrayListSerializationControl;
import uk.co.boots.dsp.messages.thirtytwor.serialization.OrderLineArrayListSerializationControl;
import uk.co.boots.dsp.messages.thirtytwor.serialization.StatusArrayListSerializationControl;
import uk.co.boots.dsp.wcs.osr.OSRBuffer;

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
		sb.append(SerializationControlIdentifiers.STATUS);
		sb.append(String.format(sc.getNumberOfEntries().getFormat(), sal.getNumberOfLines()));
		sb.append(String.format(sc.getStatusLength().getFormat(), sal.getStatusLength()));
		sal.getStatusList().forEach(status -> sb.append(status.getStatus()));
		return sb;
	}

	private StringBuffer processOrderDetail(OrderDetail od, StringBuffer sb,
			OrderLineArrayListSerializationControl sc) {
		OperatorArrayListSerializationControl oc = operatorArrayListSerializationControl;

		if (od == null)
			return sb;

		od.setPlasticBagIdLength(OrderLineArrayListSerializationControl.PLASTIC_BAG_ID_DATA_LENGTH);
		od.setProductBarcodeLength(OrderLineArrayListSerializationControl.BARCODE_DATA_LENGTH);
		od.setTimestampLength(OrderLineArrayListSerializationControl.TIMESTAMP_DATA_LENGTH);
		od.setRoleIdLength(OrderLineArrayListSerializationControl.ROLE_ID_DATA_LENGTH);
		od.setOperatorIdLength(OrderLineArrayListSerializationControl.OPERATOR_ID_DATA_LENGTH);
		od.setStatusLength(OrderLineArrayListSerializationControl.STATUS_DATA_LENGTH);


		sb.append(SerializationControlIdentifiers.ORDER_LIST_32R);
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
		sb.append(String.format(sc.getOperatorIdInfo().getFormat(), od.getOperatorIdLength()));
		sb.append(String.format(sc.getRoleIdInfo().getFormat(), od.getRoleIdLength()));
		sb.append(String.format(sc.getTimestampInfo().getFormat(), od.getTimestampLength()));
		sb.append(String.format(sc.getStatusInfo().getFormat(), od.getStatusLength()));

		List<OrderLine> ola = od.getOrderLines();
		if (ola == null || ola.size() == 0)
			return sb;
		ola.forEach(line -> processOrderLine(line, sb));
		return sb;
	}

	private void processOrderLine(OrderLine ol, StringBuffer sb) {
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
			sb = processGsOne(ol.getGsOneDetail(), sb, gsOneArrayListSerializationControl);
		
		
		
		
		sb = processOperators(ol.getOperatorDetail(), sb, operatorArrayListSerializationControl);
		if (ol.getStatus() == null || ol.getStatus().length() == 0) {
			ol.setStatus("30");
		}
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
