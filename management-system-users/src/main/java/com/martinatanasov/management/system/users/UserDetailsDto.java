package com.martinatanasov.management.system.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.martinatanasov.management.system.roles.Role;

import java.time.LocalDateTime;
import java.util.Collection;

public record UserDetailsDto(String userId,
                             String email,
                             String fullName,
                             @JsonIgnore Collection<Role> roles,
                             LocalDateTime createdDate) {

}
