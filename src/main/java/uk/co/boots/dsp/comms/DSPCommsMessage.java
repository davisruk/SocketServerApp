package uk.co.boots.dsp.comms;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.base.entity.RawMessage;
import uk.co.boots.dsp.messages.base.entity.Tote;

@AllArgsConstructor
@Data
public class DSPCommsMessage {
	private RawMessage rawMessage;
	private MessageResponseHandler responsehandler;
	private Tote tote;
}
