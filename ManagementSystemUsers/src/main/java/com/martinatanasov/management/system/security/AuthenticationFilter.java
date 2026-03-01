package com.martinatanasov.management.system.security;

import com.martinatanasov.management.system.users.UserDetailsDto;
import com.martinatanasov.management.system.users.UserLoginDto;
import com.martinatanasov.management.system.users.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment environment;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment environment) {
        super(authenticationManager);
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginDto userLoginDto = new JsonMapper().readValue(request.getInputStream(), UserLoginDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDto.email(),
                            userLoginDto.password(),
                            new ArrayList<>()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {
        //Get username
        String username = ((User) authResult.getPrincipal()).getUsername();
        //Get user credentials
        UserDetailsDto userDetailsDto = userService.findByEmailAndFullEnabled(username);

        Instant timeNow = Instant.now();

//        List<String> authorities = authResult.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .filter(auth -> !auth.startsWith("FACTOR_"))
//                .collect(Collectors.toCollection(ArrayList::new));

        List<String> permissions = userDetailsDto.roles()
                .stream()
                .flatMap(role ->
                        Stream.concat(
                                // Add roles with prefix ROLE_
                                Stream.of("ROLE_" + role.getName().name()),
                                // Add authorities
                                role.getAuthorities()
                                        .stream()
                                        .map(a -> a.getName().name())
                        )
                )
                .toList();

        log.info("\t\nUser Roles and Authorities: {}", permissions);

        //Generate Token
        String token = Jwts.builder()                       // (1)
                //Set Optional header
                .header()                                   // (2) optional
                .keyId("KeyId")
                .and()
                .expiration(Date.from(timeNow.plusMillis(Long.parseLong(environment.getProperty("token.expiration-time")))))
                .issuedAt(Date.from(timeNow))
                .subject(userDetailsDto.userId())           // (3) JSON Claims, or
                //Add user's authorities
                .claim("authorities", permissions)
                //.content(aByteArray, "text/plain")        //     any byte[] content, with media type
                //Add SecretKey (token) and algorithm
                .signWith(
                        //Set SecretKey from the token
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(environment.getProperty("token.secret-key"))),
                        //Set the Algorithm
                        Jwts.SIG.HS512
                )                                           // (4) if signing, or
                //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting
                .compact();

        //Add the token to the response as header
        response.addHeader(HttpHeaders.AUTHORIZATION, token);
        response.addHeader("userId", userDetailsDto.userId());

        //Create simple JSON with the token
        String jsonResponse = new JsonMapper().writeValueAsString(Map.of("token", token));

        //Add the token as response body
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

}
