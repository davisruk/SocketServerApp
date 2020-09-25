package uk.co.boots.messages;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerializerFactory {
	@Autowired
	private List<Serializer> serializers;

	public Optional<Serializer> getSerializer(String messageType) {
		return serializers.stream().filter(service -> service.canHandle(messageType)).findFirst();
	}

}
