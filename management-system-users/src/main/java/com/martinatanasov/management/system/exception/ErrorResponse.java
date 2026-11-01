package com.martinatanasov.management.system.exception;

import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;

@NullMarked
record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
)  {

}
