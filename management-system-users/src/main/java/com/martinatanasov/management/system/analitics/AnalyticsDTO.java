package com.martinatanasov.management.system.analitics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnalyticsDTO(
        @NotNull
        @JsonIgnore
        Long id,
        @NotNull
        @NotBlank
        @Size(max = 36)
        String userId) {
}

