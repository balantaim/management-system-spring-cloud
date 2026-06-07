package com.martinatanasov.management.system.analitics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnalyticsDTO(
        @NotNull
        Long id,
        @NotNull
        @NotBlank
        @Size(max = 36)
        String userId) {
}

