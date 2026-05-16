package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RefreshScope
@Service
public class JwtService implements AsymmetricJwtService {

    @Value("${encrypt.key-store.location}")
    private Resource KEY_STORE_LOCATION;

    @Value("${encrypt.key-store.password}")
    private String KEY_STORE_PASSWORD;

    @Value("${encrypt.key-store.alias}")
    private String KEY_STORE_ALIAS;

    @Value("${encrypt.key-store.secret}")
    private String KEY_STORE_SECRET;

    @Value("${token.expiration-time}")
    @Getter
    private Long TOKEN_EXPIRATION_TIME;

    @Value("${token.issuer}")
    private String TOKEN_ISSUER;
    private KeyPair keyPair;
    
    @PostConstruct
    public void init() {
        this.keyPair = loadKeyPair();
        log.info("RSA key pair loaded from keystore alias '{}'", KEY_STORE_ALIAS);
    }

    private KeyPair loadKeyPair() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(KEY_STORE_LOCATION.getInputStream(), KEY_STORE_PASSWORD.toCharArray());

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEY_STORE_ALIAS, KEY_STORE_SECRET.toCharArray());

            PublicKey publicKey = keyStore.getCertificate(KEY_STORE_ALIAS).getPublicKey();

            return new KeyPair(publicKey, privateKey);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load JWT key pair from keystore", e);
        }
    }

    @Override
    public PrivateKey getSigningKey() {
        return keyPair.getPrivate();
    }

    @Override
    public PublicKey getVerificationKey() {
        return keyPair.getPublic();
    }

    //Extract Claims
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getVerificationKey())   // ← PublicKey, not SecretKey
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
                .issuer(TOKEN_ISSUER)
                .claim("authorities", authorities)
                .issuedAt(Date.from(timeNow))
                .expiration(Date.from(timeNow.plusMillis(TOKEN_EXPIRATION_TIME)))
                .signWith(getSigningKey(), Jwts.SIG.RS256)
                .compact();
    }

}