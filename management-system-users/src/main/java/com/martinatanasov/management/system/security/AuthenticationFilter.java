package com.martinatanasov.management.system.security;

import com.martinatanasov.management.system.user.UserDetailsDto;
import com.martinatanasov.management.system.user.UserLoginDto;
import com.martinatanasov.management.system.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final AsymmetricJwtService jwtService;
    private final Validator validator;

    AuthenticationFilter(AuthenticationManager authenticationManager,
            UserService userService,
            AsymmetricJwtService jwtService,
            Validator validator) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
        this.validator = validator;
    }

    @NonNull
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        UserLoginDto credentials = getCredentialsFromAuthorization(authorizationHeader);
        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(credentials);

        if (!violations.isEmpty()) {
            throw new BadCredentialsException("Invalid login credentials");
        }

        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.email(),
                        credentials.password(),
                        new ArrayList<>()
                )
        );
    }

    private UserLoginDto getCredentialsFromAuthorization(String authorizationHeader) {
        try {
            String base64Credentials = authorizationHeader.substring("Basic ".length());

            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);

            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new BadCredentialsException("Invalid Basic Authentication credentials");
            }

            return new UserLoginDto(values[0], values[1]);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid Basic Authentication credentials", e);
        }
    }

    @Override
    protected void successfulAuthentication(@NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull FilterChain chain,
            Authentication authResult) throws IOException {
        //Get username
        String username = ((User) authResult.getPrincipal()).getUsername();
        //Get user credentials
        UserDetailsDto userDetailsDto = userService.findByEmailAndFullEnabled(username);
        //Create subject userId
        String subject = userDetailsDto.userId();

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

        // Generate access token and refresh token
        String accessToken = jwtService.generateAccessToken(subject, permissions);
        String refreshToken = jwtService.generateRefreshToken(subject);
        log.debug("\t\nAccess and Refresh token generated for subject: {}", subject);

        // Write tokens to response headers
        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.addHeader("User-Id", subject);

        //Create simple JSON with the tokens
        String jsonResponse = new JsonMapper().writeValueAsString(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        ));

        //Add the token as response body
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

}
