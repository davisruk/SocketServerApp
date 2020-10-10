package uk.co.boots.dsp.messages.thirtytwor;

import org.springframework.stereotype.Component;

import lombok.Getter;
import uk.co.boots.dsp.messages.SerialisationControlField;

@Component
@Getter
public class StatusArrayListSerializationControl {
	private final SerialisationControlField identifierInfo = new SerialisationControlField ("%c", 0, 1);
	private final SerialisationControlField numberOfEntries = new SerialisationControlField ("%02d", identifierInfo.getNextOffset(), 2);
	private final SerialisationControlField statusLength = new SerialisationControlField ("%02d", numberOfEntries.getNextOffset(), 2);
	
	public int getSize (ToteStatusDetail tsd) {
		return identifierInfo.getSize() + numberOfEntries.getSize() + statusLength.getSize() + (tsd.getNumberOfLines() * tsd.getStatusLength());
	}
}
