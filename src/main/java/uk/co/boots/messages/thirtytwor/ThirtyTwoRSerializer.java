package uk.co.boots.messages.thirtytwor;

import org.springframework.stereotype.Component;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.Serializer;
import uk.co.boots.messages.shared.Tote;

@Component
public class ThirtyTwoRSerializer implements Serializer {

	@Override
	public boolean canHandle(String messageType) {
		return "32RLong".equals(messageType);
	}

	@Override
	public byte[] serialize(BasicMessage msg) {
		// TODO Auto-generated method stub
		Tote t = (Tote) msg;
		return t.toString().getBytes();
	}

}
