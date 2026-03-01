package com.martinatanasov.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class UserToken {

    private String token;

    public void clear() { this.token = null; }

    public boolean hasToken() { return token != null; }

}