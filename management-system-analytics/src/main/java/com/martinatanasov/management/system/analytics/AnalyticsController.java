package com.martinatanasov.management.system.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    @GetMapping("/api/analytics/{userId}")
    public String getAnalytics(@PathVariable String userId) {
        return "Success! " + userId;
    }

}
