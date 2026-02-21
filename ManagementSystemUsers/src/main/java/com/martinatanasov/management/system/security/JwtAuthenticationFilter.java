package com.martinatanasov.management.system.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));
        String secret = environment.getProperty("token.secret-key");
        String prefix = environment.getProperty("authorization.token.header.prefix");
        assert secret != null;
        assert prefix != null;

        if (authHeader == null || !authHeader.startsWith(prefix)) {
            log.error("JWT header config is missing in environment properties or Authorization prefix is not presented");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(prefix.length());

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
        } catch (SignatureException ex) {
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
