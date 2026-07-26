package com.martinatanasov.management.system.limiter;

public class UserLimitExceeded extends RuntimeException {

    public UserLimitExceeded(String message) {
        super(message);
    }

}
