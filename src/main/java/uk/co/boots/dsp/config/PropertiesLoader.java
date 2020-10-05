package uk.co.boots.dsp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//class that enables non managed classes to access application properties
public class PropertiesLoader {
	public static Properties loadProperties(String resourceFileName) throws IOException {
		Properties configuration = new Properties();
		InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(resourceFileName);
		configuration.load(inputStream);
		inputStream.close();
		return configuration;
	}
}
