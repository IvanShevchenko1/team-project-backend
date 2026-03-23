package org.shevchenko.teamprojectbackend.dto.passwordResetToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank(message = "Потрібен токен")
        String token,

        @NotBlank(message = "Вкажіть новий пароль")
        @Size(
                min = 8,
                max = 20,
                message = "Пароль має містити від 8 до 20 символів"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_\\-]).*$",
                message = "Пароль має містити велику літеру, малу літеру, цифру та спеціальний символ"
        )
        String newPassword

        /*@NotBlank(message = "Потрібне підтвердження паролю")
        String confirmPassword*/
) {
}