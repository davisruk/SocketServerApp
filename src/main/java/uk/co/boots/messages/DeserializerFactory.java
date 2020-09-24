package uk.co.boots.messages;

import java.util.Map;

import uk.co.boots.messages.twelven.TwelveNDeserializer;

public class DeserializerFactory{
	private static Map<String, Deserializer> deserializers = Map.of("12N", new TwelveNDeserializer()); 

	public static Deserializer getDeserializerFor(String messageType) {
		return deserializers.get(messageType);
	}
}
