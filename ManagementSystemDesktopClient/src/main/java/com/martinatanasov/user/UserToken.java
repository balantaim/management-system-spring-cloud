package com.martinatanasov.user;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Singleton
public class UserToken {

    private String accessToken;
    private String refreshToken;

    public void clear() {
        this.accessToken = null;
        this.refreshToken = null;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean hasRefreshToken() {
        return refreshToken != null;
    }

    public void update(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}