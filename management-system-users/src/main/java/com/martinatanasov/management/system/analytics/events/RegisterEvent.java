package com.martinatanasov.management.system.analytics.events;

import java.time.LocalDateTime;

public record RegisterEvent(String email, LocalDateTime createdDate) {

}
