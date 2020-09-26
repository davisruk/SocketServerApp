package uk.co.boots.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.messages.SerialisationControlField;

@Component
@Getter
public class StatusArrayListSerializationControl {

	private final char identifier = 'O';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%c", 0, 1);
	private final SerialisationControlField numberOfEntries = new SerialisationControlField ("%02d", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField statusLength = new SerialisationControlField ("%02d", numberOfEntries.getNextOffset(), 2);
}
