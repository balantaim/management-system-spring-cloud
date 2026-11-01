package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.event.LoginEvent;
import com.martinatanasov.management.system.analytics.event.RegisterEvent;

import java.util.List;

public interface AnalyticsService {

    void storeLoginEvent(String eventId, LoginEvent loginEvent);

    void storeRegisterEvent(String eventId, RegisterEvent registerEvent);

    List<AnalyticsDto> getEventsByEmail(String email);

}
