package com.martinatanasov.management.system.security;

import com.martinatanasov.management.system.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /// Argon2id is used instead of bcryptPasswordEncoder (for more control over passwordEncoder add dependency for artifactId \`password4j\`)
    /// ToDo: Implement the following security configurations:
    /// OneTimeToken
    /// webAuthn() -> Passkey
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Value("${security.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        //AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));

        //Create AuthenticationManager
        AuthenticationManager authenticationManager = createCustomAuthenticationManager(http);
        //Create AuthenticationFilter and set default login url
        AuthenticationFilter authenticationFilter = getAuthenticationFilter(authenticationManager);

        http
                .authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/api/users/register", "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/analytics/**").hasRole("ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers((headers) -> headers
                        //.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .addFilter(authenticationFilter)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /// This is example for CorsConfigurationSource in WebMVC
    /// We handle the CorsConfigurationSource inside API Gateway
    /// It is safe to remove it and add .cors(Customizer.withDefaults()) inside securityFilterChain
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Add list of allowed origins
        config.setAllowedOrigins(allowedOrigins);
        // Add list of allowed headers
        config.setAllowedHeaders(List.of("Authorization", "User-Id", "Client-Platform-Id"));
        // Add list of allowed methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private AuthenticationManager createCustomAuthenticationManager(HttpSecurity http) {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, jwtService);
        //Change the default url: "/login"
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url", "/auth/login"));
        return authenticationFilter;
    }

}
