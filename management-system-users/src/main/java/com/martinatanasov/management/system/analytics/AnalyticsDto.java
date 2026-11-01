package com.martinatanasov.management.system.analytics;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

record AnalyticsDto(
        @NotNull
        Long id,
        @NotNull
        String eventIdentifier,
        @NotNull
        String platformId,
        @NotNull
        LocalDateTime createdDate) {
}

