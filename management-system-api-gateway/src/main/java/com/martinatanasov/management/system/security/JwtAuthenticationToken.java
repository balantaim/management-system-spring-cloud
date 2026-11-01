package com.martinatanasov.management.system.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String token;

    //Unauthenticated
    JwtAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
        this.principal = null;
        setAuthenticated(false);
    }

    //Authenticated
    JwtAuthenticationToken(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}