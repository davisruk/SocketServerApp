package uk.co.boots;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import uk.co.boots.server.ReceiveServer;
import uk.co.boots.server.SendServer;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    SendServer sendServer;

    @Autowired
    ReceiveServer receiveServer;

    @Override
    public void run(String... args) throws Exception {

	      String[] beanNames = appContext.getBeanDefinitionNames();
	      Arrays.sort(beanNames);
	      for (String beanName : beanNames) {
	        System.out.println(beanName);
	      }
    	
    	sendServer.startServer();
        receiveServer.startServer();
    }
}