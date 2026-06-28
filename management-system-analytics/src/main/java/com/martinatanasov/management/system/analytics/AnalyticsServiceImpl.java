package com.martinatanasov.management.system.analytics;


import com.martinatanasov.management.system.analytics.events.LoginEvent;
import com.martinatanasov.management.system.analytics.events.RegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Transactional
    @Override
    public void storeLoginEvent(String eventId, LoginEvent loginEvent) {
        Analytics analytics = analyticsRepository.save(
                Analytics.builder()
                        .email(loginEvent.email())
                        .eventIdentifier(eventId)
                        .platformId(loginEvent.platformId())
                        .createdDate(loginEvent.createdDate())
                        .build()
        );
        log.info("Storing login event for analytics {}", analytics);
    }

    @Transactional
    @Override
    public void storeRegisterEvent(String eventId, RegisterEvent registerEvent) {
        Analytics analytics = analyticsRepository.save(
                Analytics.builder()
                        .email(registerEvent.email())
                        .eventIdentifier(eventId)
                        .platformId(registerEvent.platformId())
                        .createdDate(registerEvent.createdDate())
                        .build()
        );
        log.info("Storing login event for analytics {}", analytics);
    }

    @Override
    public List<AnalyticsDTO> getEventsByEmail(String email) {
        List<Analytics> userData = analyticsRepository.findAllByEmail(email);
        log.info("Found :{}", userData);
        return userData.stream()
                .map(user -> new AnalyticsDTO(
                        user.getId(),
                        user.getEventIdentifier(),
                        user.getPlatformId(),
                        user.getCreatedDate()
                ))
                .toList();
    }
}
