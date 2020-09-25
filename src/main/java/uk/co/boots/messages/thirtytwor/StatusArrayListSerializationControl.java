package uk.co.boots.messages.thirtytwor;

import uk.co.boots.messages.SerialisationControlField;

public class StatusArrayListSerializationControl {

	private final char identifier = 'O';

	private final SerialisationControlField identifierInfo = new SerialisationControlField ("identifier", 0, 1);
	private final SerialisationControlField numberOfEntries = new SerialisationControlField ("numberOfEntries", identifierInfo.getNextOffset(), 3);
	private final SerialisationControlField statusLength = new SerialisationControlField ("numberOfEntries", numberOfEntries.getNextOffset(), 2);
}
