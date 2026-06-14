package com.martinatanasov.management.system.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/// Tracks the user platform to return correct preferences on login and sends
/// telemetry to the Analytics service. See [ClientPlatformId] for platform details.
@Component
// Runs early, after CORS
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ClientPlatformIdFilter implements WebFilter {

    private static final String CLIENT_PLATFORM_ID_HEADER_NAME = "Client-Platform-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();

        // Only enforce on POST
        if (method != HttpMethod.POST) {
            return chain.filter(exchange);
        }

        String clientPlatformId = exchange.getRequest()
                .getHeaders()
                .getFirst(CLIENT_PLATFORM_ID_HEADER_NAME);

        // Missing header
        if (clientPlatformId == null || clientPlatformId.isBlank()) {
            return rejectRequest(exchange, "Missing required header: " + CLIENT_PLATFORM_ID_HEADER_NAME);
        }

        // Header present but value not allowed
        if (!ClientPlatformId.isValid(clientPlatformId)) {
            return rejectRequest(exchange, "Invalid value for header: " + CLIENT_PLATFORM_ID_HEADER_NAME);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange, String reason) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {"error": "Forbidden", "reason": "%s"}
                """.formatted(reason);

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

}
