package com.martinatanasov.management.system.analytics.events;

import java.time.LocalDateTime;

public record LoginEvent(String email, String platformId, LocalDateTime createdDate) {

}
