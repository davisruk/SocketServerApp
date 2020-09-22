package uk.co.boots.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import uk.co.boots.osr.OSRConfig;

@Configuration
@EnableAsync ( proxyTargetClass=true)
public class ServerConfig {
    @Bean
    @ConfigurationProperties("osr")
    public OSRConfig osrConfig() {
        return new OSRConfig();
    }	
}
