package com.martinatanasov.user;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RegisterCredentialsDto(String email, String fullName, String password) {

}
