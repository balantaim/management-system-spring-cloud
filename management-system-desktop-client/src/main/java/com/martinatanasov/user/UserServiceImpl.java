package com.martinatanasov.user;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static com.martinatanasov.config.RestClientConfig.CLIENT_PLATFORM_ID;
import static com.martinatanasov.config.RestClientConfig.CLIENT_PLATFORM_ID_HEADER;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Singleton
class UserServiceImpl implements UserService {

    @Inject
    // Get the specific rest client by id
    @Named("rest-client")
    private HttpClient httpClient;
    private final UserToken userToken;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String LOGIN_API = "/auth/login";
    private static final String REFRESH_API = "/auth/refresh";
    private static final String REGISTER_API = "/api/users/register";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    @Override
    public int login(String email, String password) {
        log.info("User email: {} password: {}", email, password);
        try {
            String basicAuthorization = getBasicAuthorization(email, password);

            HttpResponse<Map<String, String>> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST(LOGIN_API, "")
                                    .header(AUTHORIZATION_HEADER,  basicAuthorization)
                                    .header(CLIENT_PLATFORM_ID_HEADER, CLIENT_PLATFORM_ID),
                            Argument.mapOf(String.class, String.class)
                    );

            Map<String, String> body = response.body();

            if (body != null && body.containsKey(KEY_ACCESS_TOKEN) && body.containsKey(KEY_REFRESH_TOKEN)) {
                userToken.update(body.get(KEY_ACCESS_TOKEN), body.get(KEY_REFRESH_TOKEN));
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
            return 500;
        }
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
                            HttpRequest.POST(REFRESH_API, Map.of(KEY_REFRESH_TOKEN, userToken.getRefreshToken()))
                                    .header(CLIENT_PLATFORM_ID_HEADER, CLIENT_PLATFORM_ID),
                            Argument.mapOf(String.class, String.class)
                    );

            Map<String, String> body = response.body();
            if (body != null && body.containsKey(KEY_ACCESS_TOKEN)) {
                userToken.updateAccessToken(body.get(KEY_ACCESS_TOKEN));
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
            return 500;
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
            HttpResponse<String> response = httpClient.toBlocking()
                    .exchange(
                            HttpRequest.POST(REGISTER_API,
                                            new RegisterCredentialsDto(email, fullName, password))
                                    .header(CLIENT_PLATFORM_ID_HEADER, CLIENT_PLATFORM_ID),
                            String.class
                    );
            log.info("Register with status code: {}", response.getStatus().getCode());
            return response.getStatus().getCode();
        } catch (HttpClientResponseException ex) {
            log.error("Server returned status: {}", ex.getStatus().getCode());
            return ex.getStatus().getCode();
        } catch (Exception ex) {
            log.error("Request failed: {}", ex.getMessage());
            return 500;
        }
    }

    private String getBasicAuthorization(String email, String password) {
        String credentials = email + ":" + password;

        return "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

}
