package uk.co.boots.messages.thirtytwor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.shared.Header;
import uk.co.boots.messages.shared.HeaderSerializationControl;
import uk.co.boots.messages.shared.OrderLine;
import uk.co.boots.messages.shared.OrderLineArrayList;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.shared.ToteIdentifier;
import uk.co.boots.messages.shared.TransportContainer;

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
		if (ti != null){
			sb = processBasicRecord(ti, sb);
		}
		
		TransportContainer tc = t.getTransportContainer();
		if (tc != null) {
			sb = processBasicRecord(tc, sb);
		}
		
		sb = processBasicRecord(t.getStartTime(), sb);
		sb = processBasicRecord(t.getEndTime(), sb);
		sb = processStatus(t.getStatus(), sb, statusArrayListSerializationControl);
		sb = processOrderLines(t.getOrderLines(), sb, orderLineArrayListSerializationControl);
		return sb.toString().getBytes();
	}
	
	private StringBuffer processHeader (Header h, StringBuffer sb, HeaderSerializationControl sc) {
		sb.append(String.format(sc.getOrderIdInfo().getFormat(), h.getOrderIdLength()));
		sb.append(String.format(sc.getSheetNumberInfo().getFormat(), h.getSheetNumberLength()));
		sb.append(h.getOrderId());
		sb.append(h.getSheetNumber());
		return sb;
	}
	
	private StringBuffer processBasicRecord (BasicRecord br, StringBuffer sb) {
		br.setPayloadLength(br.getPayload().length());
		sb.append(br.getIdentifier());
		sb.append(String.format(BasicRecord.fieldLengthInfo.getFormat(), br.getPayloadLength()));
		sb.append(br.getPayload());
		return sb;
		
	}
	
	private StringBuffer processStatus (StatusArrayList sal, StringBuffer sb, StatusArrayListSerializationControl sc) {
		sal.setStatusLength(4);
		sb.append(sc.getIdentifier());
		sb.append(String.format(sc.getNumberOfEntries().getFormat(), sal.getNumberOfLines()));
		sb.append(String.format(sc.getStatusLength().getFormat(), sal.getStatusLength()));
		sal.forEach(status -> sb.append(status.getStatus()));
		return sb;
	}
	
	private StringBuffer processOrderLines(OrderLineArrayList ola, StringBuffer sb, OrderLineArrayListSerializationControl sc) {
		OperatorArrayListSerializationControl oc = sc.getOperatorArrayListSerializationControl();
		
		// Refactor - These are not set in the 12N, should really get this info from the serialization controller
		ola.setPlasticBagIdLength(8);
		ola.setProductBarcodeLength(13);
		ola.setTimestampLength(17);
		ola.setRoleIdLength(20);
		ola.setOperatorIdLength(8);
		ola.setStatusLength(2);
		// end of refactor
		
		sb.append(orderLineArrayListSerializationControl.getIdentifier());
		sb.append(String.format(sc.getNumberOrderLinesInfo().getFormat(), ola.getNumberOfOrderLines()));
		sb.append(String.format(sc.getOrderLineRefInfo().getFormat(), ola.getOrderLineReferenceNumberLength()));
		sb.append(String.format(sc.getOrderLineTypeInfo().getFormat(), ola.getOrderLineTypeLength()));
		sb.append(String.format(sc.getPharmacyIdInfo().getFormat(), ola.getPharmacyIdLength()));
		sb.append(String.format(sc.getPatientIdInfo().getFormat(), ola.getPatientIdLength()));
		sb.append(String.format(sc.getPrescriptionIdInfo().getFormat(), ola.getPrescriptionIdLength()));
		sb.append(String.format(sc.getPlasticBagIdInfo().getFormat(), ola.getPlasticBagIdLength()));
		sb.append(String.format(sc.getProductIdInfo().getFormat(), ola.getProductIdLength()));
		sb.append(String.format(sc.getNumPacksInfo().getFormat(), ola.getNumPacksLength()));
		sb.append(String.format(sc.getNumPillsInfo().getFormat(), ola.getNumPillsLength()));
		sb.append(String.format(sc.getProductBarcodeInfo().getFormat(), ola.getProductBarcodeLength()));
		sb.append(String.format(oc.getOperatorIdInfo().getFormat(), ola.getOperatorIdLength()));
		sb.append(String.format(oc.getRoleIdInfo().getFormat(), ola.getRoleIdLength()));
		sb.append(String.format(oc.getTimestampInfo().getFormat(), ola.getTimestampLength()));
		sb.append(String.format(sc.getStatusInfo().getFormat(), ola.getStatusLength()));
		ola.forEach(line -> processOrderLine(line, sb));
		return sb;
	}

	private void processOrderLine (OrderLine ol, StringBuffer sb) {
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
		sb = processOperators(ol.getOperators(), sb, operatorArrayListSerializationControl);
		ol.setStatus("30");
		sb.append(ol.getStatus());
	}
	
	private StringBuffer processOperators (OperatorArrayList oal, StringBuffer sb, OperatorArrayListSerializationControl sc) {
		sb.append(String.format("%02d", oal.getNumberOfLines()));
		for (int i = 0; i < oal.size(); i++) {
			OperatorLine ol = oal.get(i);
			sb.append(ol.getOperatorId());
			sb.append(ol.getRoleId());
			sb.append(ol.getTimestamp());
			
		}
/*
		oal.forEach(line -> {
			System.out.println(line);
			sb.append(line.getOperatorId());
			sb.append(line.getRoleId());
			sb.append(line.getTimestamp());
		});
	*/
		return sb;
	}
}
