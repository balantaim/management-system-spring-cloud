package com.martinatanasov.management.system.analitics;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
class AnalyticsServiceImpl implements AnalyticsService {

    private final RestClient restClient;

    public AnalyticsServiceImpl(@LoadBalanced RestClient.Builder loadBalanced) {
        this.restClient = loadBalanced.baseUrl("http://management-system-analytics").build();
    }

    @Override
    public String getUserMetrics(String userId) {
        return restClient.get()
                .uri("/api/analytics/" + userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
