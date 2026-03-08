package com.martinatanasov.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final RestClient restClient;
    private final UserToken userToken;

    @Override
	public int login(String email, String password) {
        log.info("User email: {} password: {}", email, password);
        ResponseEntity<Void> response;
        try {
            response = restClient.post()
                    .uri("/auth/login")
                    .body(new LoginCredentialsDto(email, password))
                    .retrieve()
                    .toBodilessEntity();

            userToken.setToken(response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

            log.info("Login with status code: {}", response.getStatusCode());
            log.info("Has token: {}", userToken.hasToken());
            return response.getStatusCode().value();
        } catch (HttpStatusCodeException ex) {
            log.error("Server returned status: {}", ex.getStatusCode());
            return ex.getStatusCode().value();
        } catch (RestClientException ex) {
            log.error("Request failed: {}", ex.getMessage());
        }
        return 500;
	}

    @Override
    public void getInfo() {
        ResponseEntity<Void> response;
        try {
            response = restClient.get()
                    .uri("/api/users/info")
                    .retrieve()
                    .toBodilessEntity();

            log.info("Response: {}", response);
        }catch (HttpStatusCodeException ex) {
            log.error("Server returned status: {}", ex.getStatusCode());
        } catch (RestClientException ex) {
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
        ResponseEntity<Void> response;
        try {
            response = restClient.post()
                    .uri("/api/users/register")
                    .body(new RegisterCredentialsDto(email, fullName, password))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Register with status code: {}", response.getStatusCode());
            return response.getStatusCode().value();
        } catch (HttpStatusCodeException ex) {
            log.error("Server returned status: {}", ex.getStatusCode());
            return ex.getStatusCode().value();
        } catch (RestClientException ex) {
            log.error("Request failed: {}", ex.getMessage());
        }
        return 500;
    }

}
