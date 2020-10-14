package uk.co.boots.dsp.messages.framework.serialization;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeserializerFactory {

	@Autowired
	private List<Deserializer> deserializers;

	public Optional<Deserializer> getDeserializer(String messageType) {
		return deserializers.stream().filter(service -> service.canHandle(messageType)).findFirst();
	}
}
