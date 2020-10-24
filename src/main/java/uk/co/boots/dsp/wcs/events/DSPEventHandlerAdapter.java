package uk.co.boots.dsp.wcs.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class DSPEventHandlerAdapter implements DSPEventHandler{
	private String name;
}
