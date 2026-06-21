package com.martinatanasov.management.system.analytics.events;


import java.time.LocalDateTime;

public record LoginEvent(String email, LocalDateTime createdDate) {

}