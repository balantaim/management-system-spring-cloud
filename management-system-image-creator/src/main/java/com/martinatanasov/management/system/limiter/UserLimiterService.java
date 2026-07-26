package com.martinatanasov.management.system.limiter;

public interface UserLimiterService {

    boolean canConvert(String userId);
    long getRemainingTokens(String userId);

}
