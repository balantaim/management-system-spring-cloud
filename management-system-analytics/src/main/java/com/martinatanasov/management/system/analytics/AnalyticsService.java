package com.martinatanasov.management.system.analytics;

import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;

import java.util.List;

public interface AnalyticsService {

    void storeLoginEvent(String eventId, LoginEvent loginEvent);

    void storeRegisterEvent(String eventId, RegisterEvent registerEvent);

    List<AnalyticsDTO> getEventsByEmail(String email);

}
