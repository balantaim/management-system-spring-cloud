package com.martinatanasov.management.system.exceptions;

import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;

@NullMarked
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
)  {

}
