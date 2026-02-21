package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Environment environment;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
        String secret = environment.getProperty("token.secret-key");
        String prefix = environment.getProperty("authorization.token.header.prefix");
        assert secret != null;
        assert prefix != null;

        if (authHeader == null || !authHeader.startsWith(prefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(prefix.length());
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> authoritiesFromToken = claims.get("authorities", List.class);

            Collection<SimpleGrantedAuthority> authorities = authoritiesFromToken.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            log.trace("\n\tToken authorities: {}", authoritiesFromToken);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Token invalid >> clear context
            log.error("Error: {}", String.valueOf(e.fillInStackTrace()));
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
