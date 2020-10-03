package uk.co.boots.osr;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.boots.messages.shared.RawMessage;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.MessageResponseHandler;

@AllArgsConstructor
@Data
public class DSPCommsMessage {
	private RawMessage rawMessage;
	private MessageResponseHandler responsehandler;
	private Tote tote;
}
