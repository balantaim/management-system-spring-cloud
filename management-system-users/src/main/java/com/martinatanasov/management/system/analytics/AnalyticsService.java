package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;

import java.util.List;

public interface AnalyticsService {

    List<AnalyticsDTO> getUserMetrics(String userId);
    void sendRegisterMessage(RegisterEvent registerEvent);
    void sendLoginMessage(LoginEvent LoginEvent);

}
