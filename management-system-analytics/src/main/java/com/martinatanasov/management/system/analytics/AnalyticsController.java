package com.martinatanasov.management.system.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    @GetMapping("/api/analytics/{userId}")
    public ResponseEntity<AnalyticsDTO> getAnalytics(@PathVariable String userId) {
        AnalyticsDTO response = new AnalyticsDTO(1L, userId);
        return ResponseEntity.ok(response);
    }

}
