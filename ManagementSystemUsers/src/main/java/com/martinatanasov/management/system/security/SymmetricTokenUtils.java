package com.martinatanasov.management.system.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class SymmetricTokenUtils {

    /**
     * Token utilities for JJWT v0.13.0
     */

    @Value("${token.secret-key}")
    private String RAW_TOKEN;

    @Bean
    public SecretKey generateRandomSecretKey() {
        return Jwts.SIG.HS512.key().build();
    }

    @Bean
    public SecretKey getTokenAsSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(RAW_TOKEN));
    }

    @Bean
    public String SecretKeyToString() {
        //In order to get the raw string you need to paste SecretKey or you can use getTokenAsSecretKey() in order to get the raw string
        return Base64.getEncoder().encodeToString(getTokenAsSecretKey().getEncoded());
    }


}
