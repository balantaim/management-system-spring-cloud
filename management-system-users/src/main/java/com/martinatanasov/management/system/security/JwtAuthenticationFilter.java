package com.martinatanasov.management.system.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String headerName;
    private final String prefix;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService, @Value("${authorization.token.header.name}") String headerName, @Value("${authorization.token.header.prefix}") String prefix) {
        this.jwtService = jwtService;
        this.headerName = headerName;
        this.prefix = prefix;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(headerName);
        if (authorizationHeader == null || !authorizationHeader.startsWith(prefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(prefix.length());

        try {
            Claims claims = jwtService.extractAllClaims(token);

            String username = jwtService.extractSubjectFromClaims(claims);
            if (username == null) {
                log.warn("JWT subject is missing");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            List<GrantedAuthority> authorities = jwtService.extractAuthorities(claims);
            log.trace("Token authorities: {}", authorities);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (JwtException ex) {
            log.warn("JWT error: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            log.error("Unexpected error during JWT processing: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
