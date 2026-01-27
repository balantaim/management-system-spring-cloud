package com.martinatanasov.management.system.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(

        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
                message = "Invalid email format"
        )
        String email,

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        @Pattern(
                regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
                message = "Full name may contain only letters and single spaces (no new lines)"
        )
        String fullName,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$",
                message = "Password must contain uppercase, lowercase, number, and special character"
        )
        String password

) {

}
