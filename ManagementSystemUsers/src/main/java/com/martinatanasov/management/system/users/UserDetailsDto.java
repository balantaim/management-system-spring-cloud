package com.martinatanasov.management.system.users;

import java.time.LocalDateTime;

public record UserDetailsDto(String userId, String email, String fullName, LocalDateTime createdDate) {

}
