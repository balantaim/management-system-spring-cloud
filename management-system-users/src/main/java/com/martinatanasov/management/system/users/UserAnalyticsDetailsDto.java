package com.martinatanasov.management.system.users;

public record UserAnalyticsDetailsDto(String userId,
                                      String email,
                                      String fullName,
                                      Boolean accountNonExpired,
                                      Boolean accountNonLocked,
                                      Boolean credentialsNonExpired,
                                      Boolean enabled
) {

}
