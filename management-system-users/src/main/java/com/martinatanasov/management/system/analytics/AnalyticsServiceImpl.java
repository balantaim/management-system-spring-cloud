package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
class AnalyticsServiceImpl implements AnalyticsService {

    private final RestClient restClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String USER_LOGIN = "user-login";
    private static final String USER_REGISTRATION = "user-registration";

    public AnalyticsServiceImpl(@LoadBalanced RestClient.Builder loadBalanced,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.restClient = loadBalanced.baseUrl("http://management-system-analytics").build();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<AnalyticsDTO> getUserMetrics(String userId) {
        return restClient.get()
                .uri("/api/analytics/{userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public void sendRegisterMessage(RegisterEvent registerEvent) {
        log.info("\n\tSending message to analytics service --->>> {}", USER_REGISTRATION);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(USER_REGISTRATION, registerEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.trace("Consumed message: email: {}, event: {}", registerEvent.email(), USER_REGISTRATION);
            } else {
                log.error("Error while sending analytics message to analytics service: {}", ex.getMessage());
            }
        });
    }

    @Override
    public void sendLoginMessage(LoginEvent loginEvent) {
        log.info("\n\tSending message to analytics service --->>> {}", USER_LOGIN);
        //We check if the Kafka successfully get the message
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(USER_LOGIN, loginEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.trace("Consumed message: email: {}, event: {}", loginEvent.email(), USER_LOGIN);
            } else {
                log.error("Error while sending analytics message to analytics service: {}", ex.getMessage());
            }
        });
    }

}
