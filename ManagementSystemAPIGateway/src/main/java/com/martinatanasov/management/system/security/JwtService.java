package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RefreshScope
@Service
public class JwtService implements JwtVerifier {

    private static final String TOKEN_TYPE_CLAIM   = "token_type";
    private static final String ACCESS_TOKEN_TYPE  = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${token.public-key-location}")
    private Resource publicKeyResource;

    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        this.publicKey = loadPublicKey();
        log.info("JWT public key loaded successfully for token verification");
    }

    private PublicKey loadPublicKey() {
        try {
            byte[] pemBytes = publicKeyResource.getInputStream().readAllBytes();

            String pem = new String(pemBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("[\\r\\n\\s]+", "")
                    .trim();

            byte[] decoded = Base64.getDecoder().decode(pem);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(new ByteArrayInputStream(decoded)).getPublicKey();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load JWT public key from: " + publicKeyResource.getFilename(), e);
        }
    }

    // Get Key Access
    @Override
    public PublicKey getVerificationKey() {
        return publicKey;
    }

    // Claims Extraction
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
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

    // Token Type
    @Override
    public boolean isAccessToken(Claims claims) {
        return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Override
    public boolean isRefreshToken(Claims claims) {
        return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    //Get Authorities
    @Override
    @SuppressWarnings("unchecked")
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
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

}