package org.shevchenko.teamprojectbackend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDto(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 20, message = "Password must be 8-20 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_\\-]).*$",
                message = "Password must contain uppercase, lowercase, digit and special character"
        )
        String newPassword

        /*@NotBlank(message = "Confirm password is required")
        String confirmPassword */
) {
}
