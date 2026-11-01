package com.martinatanasov.management.system.user;

public record UserAnalyticsDetailsDto(String userId,
                                      String email,
                                      String fullName,
                                      Boolean accountNonExpired,
                                      Boolean accountNonLocked,
                                      Boolean credentialsNonExpired,
                                      Boolean enabled
) {

}
