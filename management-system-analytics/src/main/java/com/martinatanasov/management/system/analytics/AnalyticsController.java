package com.martinatanasov.management.system.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/api/analytics/{email}")
    public ResponseEntity<List<AnalyticsDTO>> getAnalytics(@PathVariable String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Check email's data: {}", email);
        List<AnalyticsDTO> analytics = analyticsService.getEventsByEmail(email);
        log.info("Analytics: {}", analytics);
        return ResponseEntity.ok(analytics);
    }

}
