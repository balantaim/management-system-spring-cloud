package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService implements SymmetricJwtService {

    @Value("${token.secret-key}")
    private String SECRET_KEY;
    @Value("${token.expiration-time}")
    private Long TOKEN_EXPIRATION_TIME;

    //Generate SecretKey from the secret
    @Override
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    //Generate random SecretKey
    @Override
    public SecretKey generateRandomSecretKey() {
        return Jwts.SIG.HS512.key().build();
    }

    //Extract Claims
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public String extractSubjectFromClaims(Claims claims) {
        return claims.getSubject();
    }

    @Override
    public Date extractExpiration(Claims claims) {
        return claims.getExpiration();
    }

    @Override
    public String extractIssuer(Claims claims) {
        return claims.getIssuer();
    }

    @Override
    public <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }

    //Get Authorities
    @SuppressWarnings("unchecked")
    @Override
    public List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<String> authorities = claims.get("authorities", List.class);

        if (authorities == null || authorities.isEmpty()) {
            log.warn("No authorities found in JWT claims for subject: {}", claims.getSubject());
            return Collections.emptyList();
        }

        log.debug("Extracted authorities from JWT: {}", authorities);

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    //Validate token
    @Override
    public boolean isTokenExpired(Claims claims) {
        return extractExpiration(claims).before(new Date());
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    //Token Generation
    @Override
    public String generateToken(String subject, List<String> authorities) {
        return generateToken(subject, authorities, Collections.emptyMap());
    }

    @Override
    public String generateToken(String subject, List<String> authorities, Map<String, Object> extraClaims) {
        Instant timeNow = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .claim("authorities", authorities)
                .issuedAt(Date.from(timeNow))
                .expiration(Date.from(timeNow.plusMillis(TOKEN_EXPIRATION_TIME)))
                .signWith(getSigningKey())
                .compact();
    }

}