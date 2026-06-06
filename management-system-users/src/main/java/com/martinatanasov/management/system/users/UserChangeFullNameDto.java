package com.martinatanasov.management.system.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserChangeFullNameDto(

        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
                message = "Invalid email format"
        )
        String email,

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        @Pattern(
                regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
                message = "Full name may contain only letters and single spaces (no new lines)"
        )
        String fullName
) {

}
