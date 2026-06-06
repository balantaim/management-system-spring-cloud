package com.martinatanasov.management.system.configs;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /// Creates a load-balanced {@link RestClient.Builder}.
    ///
    /// Service URLs may use a Eureka service name (for example,
    /// {@code http://management-system-analytics/api/analytics}). The actual host and port
    /// are resolved through Eureka service discovery, and a service instance is
    /// selected by Spring Cloud LoadBalancer.
    ///
    /// @return a load-balanced {@link RestClient.Builder}
    @Bean
    @LoadBalanced
    RestClient.Builder loadBalanced() {
        return RestClient.builder();
    }

    /// Creates the default {@link RestClient.Builder}.
    ///
    /// This builder performs direct HTTP calls and does not use Eureka service
    /// discovery or client-side load balancing.
    ///
    /// @return the default {@link RestClient.Builder}
    @Bean
    @Primary
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}
