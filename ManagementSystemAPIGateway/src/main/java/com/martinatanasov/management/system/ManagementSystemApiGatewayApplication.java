package com.martinatanasov.management.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;

/**
 * Exclude {@link ReactiveUserDetailsServiceAutoConfiguration} since the API Gateway
 * delegates authentication to the UserDetails microservice via JWT validation only.
 */

@SpringBootApplication(exclude = { ReactiveUserDetailsServiceAutoConfiguration.class })
public class ManagementSystemApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementSystemApiGatewayApplication.class, args);
	}

}
