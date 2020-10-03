package uk.co.boots.osr;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.boots.messages.shared.Tote;

@Data
@AllArgsConstructor
public class ToteEvent {
	public static enum EventType {TOTE_ORDER_RECEIVED,
									TOTE_ORDER_PERSISTED,
									TOTE_RELEASED_FROM_OSR,
									TOTE_RELEASED_FOR_DELIVERY, TOTE_ACTIVATED, TOTE_DEACTIVATED}
	private EventType eventType;
	private Tote tote;
}
