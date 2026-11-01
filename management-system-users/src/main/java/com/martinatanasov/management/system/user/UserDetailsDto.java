package com.martinatanasov.management.system.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.role.Role;

import java.time.LocalDateTime;
import java.util.Collection;

public record UserDetailsDto(String userId,
                             String email,
                             String fullName,
                             @JsonIgnore Collection<Role> roles,
                             LocalDateTime createdDate) {

}
