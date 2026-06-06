package com.martinatanasov.management.system.security;

import com.martinatanasov.management.system.users.UserDetailsDto;
import com.martinatanasov.management.system.users.UserLoginDto;
import com.martinatanasov.management.system.users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @NonNull
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
            throw new AuthenticationServiceException("Failed to parse authentication request", e);
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
        response.addHeader("userId", subject);

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
