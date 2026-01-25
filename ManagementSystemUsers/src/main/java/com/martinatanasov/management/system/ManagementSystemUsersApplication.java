package com.martinatanasov.management.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ManagementSystemUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementSystemUsersApplication.class, args);
	}

}
