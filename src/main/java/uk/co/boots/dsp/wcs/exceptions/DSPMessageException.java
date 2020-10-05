package uk.co.boots.dsp.wcs.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.boots.dsp.comms.DSPCommsMessage;

@Data
@AllArgsConstructor
public class DSPMessageException extends Exception {
	String message;
}
