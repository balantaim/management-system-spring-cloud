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
	public int login(String email, char[] password) {
        log.info("User email: {}, password: {}", email, new String(password));
        ResponseEntity<Void> response;
        try {
            response = restClient.post()
                    .uri("/auth/login")
                    .body(new LoginRequest(email, new String(password)))
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

}
