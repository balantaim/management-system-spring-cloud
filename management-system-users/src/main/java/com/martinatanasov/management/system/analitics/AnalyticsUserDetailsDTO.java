package com.martinatanasov.management.system.analitics;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AnalyticsUserDetailsDTO(
        @JsonIgnore
        Long id,
        String email,
        String fullName,
        String userId,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean enabled
) {

}
