package uk.co.boots.messages.thirtytwor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import uk.co.boots.config.PropertiesLoader;
import uk.co.boots.messages.shared.Tote;
import uk.co.boots.server.MessageResponseHandler;

public class FortyTwoRProcessor implements MessageResponseHandler {
	private int messageTypePos;
	private int messageTypeLength;

	private DataInputStream din;
	private Tote t;

	public FortyTwoRProcessor(Tote tote) {
		this.t = tote;
		try {
			Properties config = PropertiesLoader.loadProperties("application.properties");
			messageTypePos = Integer.parseInt(config.getProperty("message_type_offset"));
			messageTypeLength = Integer.parseInt(config.getProperty("message_type_length"));
		} catch (IOException ioe) {
			System.out.println("Failed to load properties");
		}
	}
		

	@Override
	public void setInput(DataInputStream din) {
		// TODO Auto-generated method stub
		this.din = din;
	}

	@Override
	public void processResponse() {
		try {

			InputStream ins = new BufferedInputStream(din);
			boolean finishedMessage = false, messageStarted = false;
			int b, bytesRead = 0;
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			while (!finishedMessage && (b = ins.read()) > 0) {
				bytesRead++;
				if (bytesRead == 1) {
					messageStarted = b == 0x0A || b == 0x20;
				}
				if (messageStarted) {
					buf.write(b);
					finishedMessage = b == 0x0D;
				} else {
					bytesRead = 0;
				}
			}
			byte[] messageBytes = buf.toByteArray();
			String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
			t.addRawMessage(messageBytes, msgType, new Date());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

}
