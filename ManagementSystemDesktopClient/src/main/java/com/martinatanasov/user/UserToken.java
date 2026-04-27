package com.martinatanasov.user;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Singleton
public class UserToken {

    private String token;

    public void clear() { this.token = null; }

    public boolean hasToken() { return token != null; }

}