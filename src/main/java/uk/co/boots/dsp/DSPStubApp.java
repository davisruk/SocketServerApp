package uk.co.boots.dsp;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DSPStubApp {

	public static void main(String[] args) {
		SpringApplication.run(DSPStubApp.class, args);
	}
	
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
	    return args -> {
	      String[] beanNames = ctx.getBeanDefinitionNames();
	      Arrays.sort(beanNames);
	      for (String beanName : beanNames) {
	        System.out.println(beanName);
	      }
	    };
	  }    	
}
