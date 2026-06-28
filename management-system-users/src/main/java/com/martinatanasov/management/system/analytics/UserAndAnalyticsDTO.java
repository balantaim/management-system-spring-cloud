package com.martinatanasov.management.system.analytics;

import java.util.List;

public record UserAndAnalyticsDTO(
        String email,
        String fullName,
        String userId,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean enabled,
        List<AnalyticsDTO> analytics
) {

}
