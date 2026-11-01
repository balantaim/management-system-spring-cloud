package com.martinatanasov.management.system.config;

import org.springframework.boot.http.client.InetAddressFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
class RestClientConfig {

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

    /// Creates an `InetAddressFilter` that protects `RestClient` from
    /// Server-Side Request Forgery (SSRF) attacks.
    /// Only public IP addresses are allowed. Requests to localhost,
    /// private, link-local, and other non-public addresses are blocked.
    /// @return an `InetAddressFilter` allowing only external addresses
    @Bean
    InetAddressFilter ssrfProtectionFilter() {
        return InetAddressFilter.externalAddresses();
    }

}
