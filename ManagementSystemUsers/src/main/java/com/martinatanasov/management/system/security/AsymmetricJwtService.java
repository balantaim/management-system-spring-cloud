package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface AsymmetricJwtService {

    PrivateKey getSigningKey();

    PublicKey getVerificationKey();

    Claims extractAllClaims(String token);

    String extractSubject(String token);

    String extractSubjectFromClaims(Claims claims);

    Date extractExpiration(Claims claims);

    String extractIssuer(Claims claims);

    <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver);

    List<GrantedAuthority> extractAuthorities(Claims claims);

    boolean isTokenExpired(Claims claims);

    boolean isTokenValid(String token);

    String generateToken(String subject, List<String> authorities);

    String generateToken(String subject, List<String> authorities, Map<String, Object> extraClaims);

}
