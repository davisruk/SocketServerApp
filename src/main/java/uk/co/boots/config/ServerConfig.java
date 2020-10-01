package uk.co.boots.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import uk.co.boots.osr.OSRConfig;

@Configuration
@EnableAsync ( proxyTargetClass=true)
public class ServerConfig {
	@Autowired
	private Environment env;
	
	@Bean
    @ConfigurationProperties("osr")
    public OSRConfig osrConfig() {
        return new OSRConfig();
    }
/*    
    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
    	
    	final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("SocketThread-");
        executor.initialize();
        return executor;
    }
*/ 
}
