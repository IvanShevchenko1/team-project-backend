package org.shevchenko.teamprojectbackend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDto(
        @NotBlank(message = "Current email is required")
        @Email(message = "Некоректний формат електронної пошти")
        String currentEmail,

        @NotBlank(message = "New email is required")
        @Email(message = "Некоректний формат електронної пошти")
        String newEmail
) {
}