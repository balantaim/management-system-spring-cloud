package com.martinatanasov.management.system.security;

import org.springframework.core.env.Environment;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

public class JwtAuthorizationWebFilter extends AuthenticationWebFilter {

    public JwtAuthorizationWebFilter(JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager, Environment environment) {
        super(jwtReactiveAuthenticationManager);
        setServerAuthenticationConverter(converter(environment));
    }

    private ServerAuthenticationConverter converter(Environment environment) {
        return exchange -> {

            String headerName = environment.getProperty("authorization.token.header.name");
            String prefix = environment.getProperty("authorization.token.header.prefix");
            assert headerName != null;
            assert prefix != null;

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(headerName);
            //Check the authorization header and prefix
            if (authorizationHeader == null || !authorizationHeader.startsWith(prefix)) {
                return Mono.empty();
            }

            String token = authorizationHeader.replace(prefix, "");

            return Mono.just(new JwtAuthenticationToken(token));
        };
    }

}
