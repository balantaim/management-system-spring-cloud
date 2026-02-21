package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Objects;

public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final Environment environment;

    public JwtReactiveAuthenticationManager(Environment environment) {
        this.environment = environment;
    }

    @Override
    @NonNull
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = Objects.requireNonNull(authentication.getCredentials()).toString();

        return Mono.fromCallable(() -> {
                    String secret = environment.getProperty("token.secret-key");
                    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

                    Claims claims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String userId = claims.getSubject();
                    if (userId == null) {
                        throw new BadCredentialsException("JWT subject is missing");
                    }

                    return new JwtAuthenticationToken(userId, List.of());
                }).cast(Authentication.class)
                .onErrorMap(ExpiredJwtException.class,
                        ex -> new BadCredentialsException("JWT expired", ex)
                )
                .onErrorMap(JwtException.class,
                        ex -> new BadCredentialsException("Invalid or expired JWT", ex)
                );
    }

}
