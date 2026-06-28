package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

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
        kafkaTemplate.send(USER_REGISTRATION, registerEvent);
    }

    @Override
    public void sendLoginMessage(LoginEvent loginEvent) {
        log.info("\n\tSending message to analytics service --->>> {}", USER_LOGIN);
        kafkaTemplate.send(USER_LOGIN, loginEvent);
    }

}
