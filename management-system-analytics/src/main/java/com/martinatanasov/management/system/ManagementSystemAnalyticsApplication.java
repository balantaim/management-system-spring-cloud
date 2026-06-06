package com.martinatanasov.management.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ManagementSystemAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementSystemAnalyticsApplication.class, args);
	}

}
