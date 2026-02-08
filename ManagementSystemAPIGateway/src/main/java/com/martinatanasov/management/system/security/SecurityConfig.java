package com.martinatanasov.management.system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
//Enable Reactive Security required for Reactive Gateway
@EnableWebFluxSecurity
public class SecurityConfig {

    //Configure reactive web filter chain
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthorizationWebFilter jwtAuthorizationWebFilter) {
        //In the reactive world session is STATELESS by default
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers
                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                )
                .authorizeExchange(request -> request
                        .pathMatchers(HttpMethod.POST, "/auth/login", "/api/users/register").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/users", "/api/users/info").permitAll()
                        .pathMatchers("/h2-console/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .addFilterAt(jwtAuthorizationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }

    @Bean
    public JwtAuthorizationWebFilter jwtAuthorizationWebFilter(Environment environment) {
        return new JwtAuthorizationWebFilter(new JwtReactiveAuthenticationManager(environment), environment);
    }

}
