package uk.co.boots.dsp;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import uk.co.boots.dsp.comms.tcp.ReceiveServer;
import uk.co.boots.dsp.comms.tcp.SendServer;
import uk.co.boots.dsp.events.DSPEventNotifier;
import uk.co.boots.dsp.events.EventLogger;
import uk.co.boots.dsp.wcs.TrackController;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    SendServer sendServer;
    @Autowired
    ReceiveServer receiveServer;
	@Autowired
	private TrackController trackController;
	@Autowired
    @Qualifier("dspEventNotifier")	
	private DSPEventNotifier dspEventNotifier;
	@Autowired
	private EventLogger eventLogger;
	
    @Override
    public void run(String... args) throws Exception {

	      String[] beanNames = appContext.getBeanDefinitionNames();
	      Arrays.sort(beanNames);
	      for (String beanName : beanNames) {
	        System.out.println(beanName);
	      }
	      dspEventNotifier.registerEventHandler(eventLogger);
	      receiveServer.startServer();
	      sendServer.startServer();
	      trackController.start();
    }
}