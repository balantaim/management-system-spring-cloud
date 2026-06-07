package com.martinatanasov.management.system.analitics;

import java.util.Optional;

public interface AnalyticsService {

    Optional<AnalyticsDTO> getUserMetrics(String userId);

}
