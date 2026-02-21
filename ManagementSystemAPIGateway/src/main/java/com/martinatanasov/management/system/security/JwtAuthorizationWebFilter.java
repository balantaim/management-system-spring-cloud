package com.martinatanasov.management.system.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class JwtAuthorizationWebFilter extends AuthenticationWebFilter {

    private final JwtService jwtService;

    public JwtAuthorizationWebFilter(JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager,
            Environment environment,
            JwtService jwtService) {
        super(jwtReactiveAuthenticationManager);
        this.jwtService = jwtService;
        setServerAuthenticationConverter(converter(environment));
        setAuthenticationFailureHandler(authenticationFailureHandler());
    }

    private ServerAuthenticationConverter converter(Environment environment) {
        return exchange -> {
            String headerName = environment.getProperty("authorization.token.header.name");
            String prefix = environment.getProperty("authorization.token.header.prefix");

            if (headerName == null || prefix == null) {
                log.error("JWT header config is missing in environment properties");
                return Mono.empty();
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(headerName);

            if (authorizationHeader == null || !authorizationHeader.startsWith(prefix)) {
                log.debug("No JWT token found in request headers for path: {}",
                        exchange.getRequest().getPath());
                return Mono.empty();
            }

            String token = authorizationHeader.replace(prefix, "").trim();

            return Mono.just(token)
                    .flatMap(t -> extractAndValidateClaims(t, exchange))
                    .onErrorResume(ExpiredJwtException.class, ex -> {
                        log.warn("JWT token expired: {}", ex.getMessage());
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired");
                    })
                    .onErrorResume(MalformedJwtException.class, ex -> {
                        log.warn("Malformed JWT token: {}", ex.getMessage());
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "JWT token is malformed");
                    })
                    .onErrorResume(SignatureException.class, ex -> {
                        log.warn("Invalid JWT signature: {}", ex.getMessage());
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT signature");
                    })
                    .onErrorResume(UnsupportedJwtException.class, ex -> {
                        log.warn("Unsupported JWT token: {}", ex.getMessage());
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "JWT token is unsupported");
                    })
                    .onErrorResume(JwtException.class, ex -> {
                        log.warn("JWT error: {}", ex.getMessage());
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
                    });
        };
    }

    private Mono<Authentication> extractAndValidateClaims(String token, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            Claims claims = jwtService.extractAllClaims(token);

            log.debug("Claims: {}", claims);

            String subject = claims.getSubject();
            List<GrantedAuthority> authorities = jwtService.extractAuthorities(claims);

            log.debug("JWT subject: {}, authorities: {}", subject, authorities);

            // Store claims in exchange attributes for downstream use
            exchange.getAttributes().put("jwt_claims", claims);
            exchange.getAttributes().put("jwt_subject", subject);

            // Use your authenticated constructor
            return (Authentication) new JwtAuthenticationToken(subject, authorities);
        });
    }

    private ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, ex) -> {
            log.warn("Authentication failed: {}", ex.getMessage());
            return writeErrorResponse(webFilterExchange.getExchange(),
                    HttpStatus.UNAUTHORIZED, ex.getMessage());
        };
    }

    private <T> Mono<T> writeErrorResponse(ServerWebExchange exchange,
            HttpStatus status,
            String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest().getPath().value();
        String timestamp = LocalDateTime.now().toString();

        // Build JSON string directly — no ObjectMapper needed
        String body = """
                {
                    "status": %d,
                    "error": "%s",
                    "message": "%s",
                    "path": "%s",
                    "timestamp": "%s"
                }
                """.formatted(status.value(), status.getReasonPhrase(), message, path, timestamp);

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer)).then(Mono.empty());
    }
}
