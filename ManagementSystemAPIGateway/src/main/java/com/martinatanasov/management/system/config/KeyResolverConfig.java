package com.martinatanasov.management.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class KeyResolverConfig {

    //Rate limit per authenticated user
    @Primary
    @Bean
    public KeyResolver userIdKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(principal -> {
                    log.debug("Rate limit key resolved to: {}", principal.getName());
                    return principal.getName();
                })
                .defaultIfEmpty("unknown");
    }

    //Rate limit per IP address (For login/register endpoints)
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .defaultIfEmpty("unknown");
    }

}
