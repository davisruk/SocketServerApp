package uk.co.boots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.co.boots.server.SocketServer;

@SpringBootApplication
public class SocketServerAppApplication implements CommandLineRunner{

	@Autowired
	SocketServer server;
	
	public static void main(String[] args) {
		SpringApplication.run(SocketServerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		server.startServer();
	}
}
