package com.martinatanasov.management.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ManagementSystemConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementSystemConfigServerApplication.class, args);
	}

}
