package com.martinatanasov.management.system.analytics;


import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Transactional
    @Override
    public void storeLoginEvent(String eventId, LoginEvent loginEvent) {
        analyticsRepository.save(
                Analytics.builder()
                        .email(loginEvent.email())
                        .eventIdentifier(eventId)
                        .platformId(loginEvent.platformId())
                        .createdDate(loginEvent.createdDate())
                        .build()
        );
    }

    @Transactional
    @Override
    public void storeRegisterEvent(String eventId, RegisterEvent registerEvent) {
        analyticsRepository.save(
                Analytics.builder()
                        .email(registerEvent.email())
                        .eventIdentifier(eventId)
                        .platformId(registerEvent.platformId())
                        .createdDate(registerEvent.createdDate())
                        .build()
        );
    }
}
