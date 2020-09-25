package uk.co.boots.messages.thirtytwor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.BasicRecord;
import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.shared.Header;
import uk.co.boots.messages.shared.HeaderSerializationControl;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.messages.shared.ToteIdentifier;
import uk.co.boots.messages.shared.TransportContainer;

@Component
public class ThirtyTwoRSerializer implements Serializer {

	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private StatusArrayListSerializationControl statusArrayListSerializationControl;
	
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
		sb = processHeader(t.getHeader(), sb);

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
		sb = processStatus(t.getStatus(), sb);
		return sb.toString().getBytes();
	}
	
	private StringBuffer processHeader (Header h, StringBuffer sb) {
		sb.append(h.getOrderIdLength());
		sb.append(h.getSheetNumberLength());
		sb.append(h.getOrderId());
		sb.append(h.getSheetNumber());
		return sb;
	}
	
	private StringBuffer processBasicRecord (BasicRecord br, StringBuffer sb) {
		br.setPayloadLength(BasicRecord.fieldLengthInfo.getSize());
		sb.append(br.getIdentifier());
		sb.append(br.getPayloadLength());
		sb.append(br.getPayload());
		return sb;
		
	}
	
	private StringBuffer processStatus (StatusArrayList sal, StringBuffer sb) {
		sal.setStatusLength(4);
		sb.append(statusArrayListSerializationControl.getIdentifier());
		sb.append(String.format("%02d", sal.getNumberOfLines()));
		sb.append(String.format("%02d", sal.getStatusLength()));
		sal.forEach(status -> sb.append(status.getStatus()));
		return sb;
	}

}
