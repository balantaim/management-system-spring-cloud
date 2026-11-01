package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.event.LoginEvent;
import com.martinatanasov.management.system.analytics.event.RegisterEvent;

import java.util.List;

public interface AnalyticsService {

    List<AnalyticsDto> getUserMetrics(String userId);
    void sendRegisterMessage(RegisterEvent registerEvent);
    void sendLoginMessage(LoginEvent LoginEvent);

}
