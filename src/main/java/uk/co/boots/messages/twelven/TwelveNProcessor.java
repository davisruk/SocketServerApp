package uk.co.boots.messages.twelven;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.boots.messages.BasicMessage;
import uk.co.boots.messages.MessageProcessor;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.osr.OSRBuffer;

@Component
public class TwelveNProcessor implements MessageProcessor {

	@Autowired
	private OSRBuffer osrBuffer;

    @Autowired
    private ObjectMapper objectMapper;		
	
	@Override
	public void process(BasicMessage m) {
		// TODO Auto-generated method stub
		Tote t = (Tote) m;
		osrBuffer.addToteMessage(t);
		try {
		 objectMapper.writeValue(new File("D:/java/12N.json"), t);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public byte[] getResponse(BasicMessage m) {
		// TODO Get the 22N Serializer and return message
		return "22N".getBytes();
	}

	@Override
	public boolean hasResponse() {
		return true;
	}

}
