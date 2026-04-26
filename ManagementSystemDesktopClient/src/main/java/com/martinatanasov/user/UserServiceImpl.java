package com.martinatanasov.user;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class UserServiceImpl implements UserService {

    private final HttpClient httpClient;
    private final UserToken userToken;

    @Override
    public int login(String email, String password) {
        log.info("User email: {} password: {}", email, password);
        try {
            HttpResponse<Void> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST("/auth/login", new LoginCredentialsDto(email, password)),
                            Void.class
                    );
            userToken.setToken(response.getHeaders().get("Authorization"));
            log.info("Login with status code: {}", response.getStatus().getCode());
            log.info("Has token: {}", userToken.hasToken());
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
    public void getInfo() {
        try {
            HttpResponse<Void> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.GET("/api/users/info"),
                            Void.class
                    );
            log.info("Response: {}", response);

        } catch (HttpClientResponseException ex) {
            log.error("Server returned status: {}", ex.getStatus().getCode());
        } catch (Exception ex) {
            log.error("Request failed: {}", ex.getMessage());
        }
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
