package uk.co.boots.dsp.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import uk.co.boots.dsp.messages.Deserializer;
import uk.co.boots.dsp.messages.DeserializerFactory;
import uk.co.boots.dsp.messages.shared.Tote;

@RestController
@RequestMapping("/utils")
public class DSPUtilsController {

	@Value("${message_type_offset}")
	private int messageTypePos;
	
	@Value("${message_type_length}")
	private int messageTypeLength;
	
	@Autowired
	private DeserializerFactory deserializerFactory;

	@PostMapping("/prettify")
    public Tote prettifyMessage(@RequestParam("file") MultipartFile file) throws IOException{
		byte[] messageBytes = file.getBytes();
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		if (messageBytes[0] != 0x0A) {
			buf.write(0x0A);
		}
		buf.write(messageBytes, 0, messageBytes.length - 1);
		if (messageBytes[messageBytes.length-1] != 0x0D) {
			buf.write(0x0D);
		}
		messageBytes = buf.toByteArray();		
		String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
		Deserializer d = deserializerFactory.getDeserializer(msgType).get();
		return (Tote) d.deserialize(messageBytes);
    }
	
	@Data
	private class PrettifyParams {
		private boolean hasStartFrame;
		private boolean hasEndFrame;
		private int repositionEndFrameBytesFromEnd;
	}
}
