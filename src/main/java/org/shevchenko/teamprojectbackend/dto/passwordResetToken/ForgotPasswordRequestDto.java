package org.shevchenko.teamprojectbackend.dto.passwordResetToken;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDto(
        @NotBlank(message = "Вкажіть електронну пошту")
        @Email(message = "Некоректний формат електронної пошти")
        String email
) {
}
