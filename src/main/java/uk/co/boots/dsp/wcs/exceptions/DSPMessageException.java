package uk.co.boots.dsp.wcs.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DSPMessageException extends Exception {
	String message;
}
