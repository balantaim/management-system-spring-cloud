package com.martinatanasov.management.system.security;

import com.martinatanasov.management.system.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class GlobalSecurityConfig {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        //AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));

        //Create AuthenticationManager
        AuthenticationManager authenticationManager = createCustomAuthenticationManager(http);
        //Create AuthenticationFilter and set default login url
        AuthenticationFilter authenticationFilter = getAuthenticationFilter(authenticationManager);

        http
                .authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/info", "/actuator/**").permitAll()
                        .requestMatchers("/h2-console/**", "/api/users").permitAll()
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
                .addFilter(authenticationFilter);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5000"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
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
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, environment);
        //Change the default url: "/login"
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url", "/auth/login"));
        return authenticationFilter;
    }

}
