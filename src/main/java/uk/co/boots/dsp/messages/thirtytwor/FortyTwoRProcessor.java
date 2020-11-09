package uk.co.boots.dsp.messages.thirtytwor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boots.dsp.config.PropertiesLoader;
import uk.co.boots.dsp.messages.MessageResponseHandler;
import uk.co.boots.dsp.messages.base.entity.Tote;
import uk.co.boots.dsp.wcs.events.handlers.EventLogger;
import uk.co.boots.dsp.wcs.exceptions.DSPMessageException;

public class FortyTwoRProcessor implements MessageResponseHandler {
	private int messageTypePos;
	private int messageTypeLength;

	private DataInputStream din;
	private Tote t;
	private Logger logger = LoggerFactory.getLogger(EventLogger.class);
	
	public FortyTwoRProcessor(Tote tote) {
		this.t = tote;
		try {
			Properties config = PropertiesLoader.loadProperties("application.properties");
			messageTypePos = Integer.parseInt(config.getProperty("message_type_offset"));
			messageTypeLength = Integer.parseInt(config.getProperty("message_type_length"));
		} catch (IOException ioe) {
			logger.error("Failed to load properties");
		}
	}
		

	@Override
	public void setInput(DataInputStream din) {
		this.din = din;
	}

	@Override
	public void processResponse() throws DSPMessageException{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		InputStream ins = new BufferedInputStream(din);
		logger.info("[FortyTwoRProcessor::processResponse] processing on thread " + Thread.currentThread().getName());
		boolean finishedMessage = false, messageStarted = false;
		int b = -1, bytesRead = 0;
		try {

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
		} catch (IOException ioe) {
			throw new DSPMessageException ("[42R Processor]" + ioe.getMessage());
		}
		
		byte[] messageBytes = buf.toByteArray();
		if (messageBytes.length == 0) {
			logger.info("[FortyTwoRProcessor::processResponse] received 0 length response");
			throw new DSPMessageException("[FortyTwoRProcessor::processResponse] received 0 length response");
		}
		
		String msgType = new String(messageBytes, messageTypePos, messageTypeLength);
		t.addRawMessage(messageBytes, msgType, new Date());
	}

}
