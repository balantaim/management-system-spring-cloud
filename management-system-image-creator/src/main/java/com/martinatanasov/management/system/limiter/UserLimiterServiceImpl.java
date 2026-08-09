package com.martinatanasov.management.system.limiter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
class UserLimiterServiceImpl implements UserLimiterService {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private static final long TOKENS_PER_DAY = 50;

    @Override
    public boolean canConvert(String userId) {
        //Create bucket if not exist
        Bucket bucket = userBuckets.computeIfAbsent(
                userId,
                this::createBucket
        );

        //Try to consume 1 token and return remaining (It is optional you could use bucket.tryConsume(1))
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        log.info("\n\tTokens --->>> Is consumed: {},\n remaining tokens: {},\n time till reset: {}",
                probe.isConsumed(), probe.getRemainingTokens(), probe.getNanosToWaitForReset());
        return probe.isConsumed();
    }

    @Override
    public long getRemainingTokens(String userId) {
        //Get bucked by userId
        Bucket bucket = userBuckets.get(userId);
        if (bucket == null) {
            log.warn("\n\tBucket not found for user: {}", userId);
            return TOKENS_PER_DAY;
        }
        log.info("\n\tGet available tokens for user: {}, remaining: {}", userId, bucket.getAvailableTokens());
        //Return available tokens
        return bucket.getAvailableTokens();
    }

    private Bucket createBucket(String userId) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(TOKENS_PER_DAY)
                        .refillGreedy(TOKENS_PER_DAY, Duration.ofDays(1)))
                .build();
    }

}
