package com.martinatanasov.user;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class UserServiceImpl implements UserService {

    @Inject
    // Get the specific rest client by id
    @Named("rest-client")
    private HttpClient httpClient;

    private final UserToken userToken;

    @Override
    public int login(String email, String password) {
        log.info("User email: {} password: {}", email, password);
        try {
            HttpResponse<Map<String, String>> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST("/auth/login", new LoginCredentialsDto(email, password)),
                            Argument.mapOf(String.class, String.class)
                    );
//            userToken.setToken(response.getHeaders().get("Authorization"));
//            log.info("Login with status code: {}", response.getStatus().getCode());
//            log.info("Has token: {}", userToken.hasToken());
//            return response.getStatus().getCode();

            Map<String, String> body = response.body();
            if (body != null && body.containsKey("access_token") && body.containsKey("refresh_token")) {
                userToken.update(body.get("access_token"), body.get("refresh_token"));
                log.info("Login successful, tokens stored");
                log.debug("Has access token: {}", userToken.hasAccessToken());
            } else {
                log.warn("Login response missing tokens in body");
            }

            log.info("Login with status code: {}", response.getStatus().getCode());
            return response.getStatus().getCode();

        } catch (HttpClientResponseException ex) {
            log.error("Server returned status: {}", ex.getStatus().getCode());
            return ex.getStatus().getCode();
        } catch (Exception ex) {
            log.error("Request failed: {}", ex.getMessage());
        }
        return 500;
    }

    @Override
    public int refreshAccessToken() {
        log.info("Attempting to refresh access token");
        if (!userToken.hasRefreshToken()) {
            log.warn("No refresh token available, user must log in again");
            return 401;
        }
        try {
            HttpResponse<Map<String, String>> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST("/auth/refresh",
                                    Map.of("refresh_token", userToken.getRefreshToken())),
                            Argument.mapOf(String.class, String.class)
                    );

            Map<String, String> body = response.body();
            if (body != null && body.containsKey("access_token")) {
                userToken.updateAccessToken(body.get("access_token"));
                log.info("Access token refreshed successfully");
            } else {
                log.warn("Refresh response missing access_token in body");
            }

            return response.getStatus().getCode();

        } catch (HttpClientResponseException ex) {
            log.error("Token refresh failed with status: {}", ex.getStatus().getCode());
            if (ex.getStatus().getCode() == 401) {
                // Refresh token is expired or invalid — force re-login
                log.warn("Refresh token rejected, clearing session");
                userToken.clear();
            }
            return ex.getStatus().getCode();
        } catch (Exception ex) {
            log.error("Refresh request failed: {}", ex.getMessage());
        }
        return 500;
    }

    @Override
    public void logout() {
        log.info("Clear the token and logout!");
        userToken.clear();
    }

    @Override
    public int register(String email, String fullName, String password) {
        log.info("User email: {} password: {} full name: {}", email, password, fullName);
        try {
            HttpResponse<Void> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST("/api/users/register",
                                    new RegisterCredentialsDto(email, fullName, password)),
                            Void.class
                    );
            log.info("Register with status code: {}", response.getStatus().getCode());
            return response.getStatus().getCode();

        } catch (HttpClientResponseException ex) {
            log.error("Server returned status: {}", ex.getStatus().getCode());
            return ex.getStatus().getCode();
        } catch (Exception ex) {
            log.error("Request failed: {}", ex.getMessage());
        }
        return 500;
    }

}
