package uk.co.boots.dsp.messages.thirtytwor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.messages.BasicMessage;
import uk.co.boots.dsp.messages.BasicRecord;
import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.Serializer;
import uk.co.boots.dsp.messages.shared.Header;
import uk.co.boots.dsp.messages.shared.HeaderSerializationControl;
import uk.co.boots.dsp.messages.shared.SerializationControlIdentifiers;
import uk.co.boots.dsp.messages.shared.Tote;

@Component
public class ThirtyTwoRShortSerializer implements Serializer{

	@Autowired
	private HeaderSerializationControl headerSerializationControl;
	@Autowired
	private StatusArrayListSerializationControl statusArrayListSerializationControl;

	@Override
	public boolean canHandle(String messageType) {
		return "32RShort".equals(messageType);	}

	@Override
	public byte[] serialize(BasicMessage msg) {
		Tote t = (Tote) msg;
		StringBuffer sb = new StringBuffer();
		sb.append("32R");
		sb = processHeader(t.getHeader(), sb, headerSerializationControl);
		sb = processBasicRecord(t.getToteIdentifier(), sb);
		sb = processBasicRecord(t.getStartTime(), sb);
		sb = processStatus(t.getStatusDetail(), sb, statusArrayListSerializationControl);
		// have to process this part of the header here because we don't know the size
		// of the message until now
		int messageLength = sb.length() + headerSerializationControl.getSizeInfo().getSize();
		sb.insert(0, String.format(headerSerializationControl.getSizeInfo().getFormat(), messageLength));
		return sb.toString().getBytes();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "32R-Short";
	}

	@Override
	public MessageResponseHandler getResponseProcessor(BasicMessage message) {
		Tote t = (Tote) message;
		return new FortyTwoRProcessor(t);
	}

	private StringBuffer processHeader(Header h, StringBuffer sb, HeaderSerializationControl sc) {
		sb.append(String.format(sc.getOrderIdInfo().getFormat(), h.getOrderIdLength()));
		sb.append(String.format(sc.getSheetNumberInfo().getFormat(), h.getSheetNumberLength()));
		sb.append(h.getOrderId());
		sb.append(h.getSheetNumber());
		return sb;
	}

	private StringBuffer processBasicRecord(BasicRecord br, StringBuffer sb) {
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
	
}
