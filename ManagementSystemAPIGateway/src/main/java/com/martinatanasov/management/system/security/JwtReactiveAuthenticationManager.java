package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    @NonNull
    public Mono<Authentication> authenticate(Authentication authentication) {
        //If it is already authenticated by the filter return it
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }

        String token = Objects.requireNonNull(authentication.getCredentials()).toString();

        return Mono.fromCallable(() -> {
                    Claims claims = jwtService.extractAllClaims(token);

                    String subject = jwtService.extractSubjectFromClaims(claims);
                    if (subject == null) {
                        throw new BadCredentialsException("JWT subject is missing");
                    }

                    List<GrantedAuthority> authorities = jwtService.extractAuthorities(claims);

                    log.debug("Authenticated subject: {}, authorities: {}", subject, authorities);

                    return (Authentication) new JwtAuthenticationToken(subject, authorities);
                })
                .onErrorMap(ExpiredJwtException.class,
                        ex -> new BadCredentialsException("JWT expired", ex))
                .onErrorMap(JwtException.class,
                        ex -> new BadCredentialsException("Invalid or expired JWT", ex));
    }

}
