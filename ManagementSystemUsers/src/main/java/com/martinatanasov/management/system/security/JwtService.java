package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${encrypt.key-store.location}")
    private Resource KEY_STORE_LOCATION;

    @Value("${encrypt.key-store.password}")
    private String KEY_STORE_PASSWORD;

    @Value("${encrypt.key-store.alias}")
    private String KEY_STORE_ALIAS;

    @Value("${encrypt.key-store.secret}")
    private String KEY_STORE_SECRET;

    @Value("${token.access-expiration}")
    @Getter
    private Long ACCESS_TOKEN_EXPIRATION;

    @Value("${token.refresh-expiration}")
    @Getter
    private Long REFRESH_TOKEN_EXPIRATION;

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
                // PublicKey, not SecretKey
                .verifyWith(getVerificationKey())
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

    @Override
    public boolean isAccessToken(Claims claims) {
        return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Override
    public boolean isRefreshToken(Claims claims) {
        return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
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

    //Generate Access Token
    @Override
    public String generateAccessToken(String subject, List<String> authorities) {
        return generateAccessToken(subject, authorities, Collections.emptyMap());
    }

    @Override
    public String generateAccessToken(String subject, List<String> authorities, Map<String, Object> extraClaims) {
        return buildToken(
                subject,
                authorities,
                extraClaims,
                ACCESS_TOKEN_TYPE,
                ACCESS_TOKEN_EXPIRATION
        );
    }

    @Override
    public String generateRefreshToken(String subject) {
        // Refresh tokens carry no authorities - they are only used to get a new access token
        return buildToken(
                subject,
                Collections.emptyList(),
                Collections.emptyMap(),
                REFRESH_TOKEN_TYPE,
                REFRESH_TOKEN_EXPIRATION
        );
    }

    private String buildToken(String subject, List<String> authorities,
            Map<String, Object> extraClaims, String tokenType, long expirationTime) {
        Instant timeNow = Instant.now();
        JwtBuilder builder = Jwts.builder()
                //Set Optional header
//                .header()
//                .keyId("KeyId")
//                .and()
                .claims(extraClaims)
                .subject(subject)
                .issuer(TOKEN_ISSUER)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(timeNow))
                //.content(aByteArray, "text/plain") //any byte[] content, with media type
                .expiration(Date.from(timeNow.plusMillis(expirationTime)))
                //Set SecretKey from the token
                //Set the Algorithm RSA-2048
                .signWith(getSigningKey(), Jwts.SIG.RS256);

        if (!authorities.isEmpty()) {
            builder.claim("authorities", authorities);
        }

        return builder.compact();
    }

}