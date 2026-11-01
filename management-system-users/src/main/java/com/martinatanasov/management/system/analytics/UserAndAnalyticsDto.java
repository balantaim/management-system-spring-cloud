package com.martinatanasov.management.system.analytics;

import java.util.List;

record UserAndAnalyticsDto(
        String email,
        String fullName,
        String userId,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean enabled,
        List<AnalyticsDto> analytics
) {

}
