package uk.co.boots.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SerialisationControlField {
	private String format;
	private int offset;
	private int size;
	
	public int getNextOffset() {
		return offset + size;
	}
}
