package com.martinatanasov.user;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record LoginCredentialsDto(String email, String password) {

}
