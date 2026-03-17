package com.martinatanasov.management.system.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserChangePasswordDto(

        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
                message = "Invalid email format"
        )
        String email,
        @NotBlank(message = "Old password is required")
        @Size(min = 8, max = 50, message = "Old password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$",
                message = "Old password must contain uppercase, lowercase, number, and special character"
        )
        String oldPassword,
        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$",
                message = "New password must contain uppercase, lowercase, number, and special character"
        )
        String newPassword

) {

}
