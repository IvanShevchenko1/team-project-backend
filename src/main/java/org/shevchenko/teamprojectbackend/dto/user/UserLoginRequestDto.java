package org.shevchenko.teamprojectbackend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(

        @NotEmpty(message = "Вкажіть електронну пошту")
        @Email(message = "Некоректний формат електронної пошти")
        String email,

        @NotEmpty(message = "Вкажіть пароль")
        @Size(
                min = 8,
                max = 20,
                message = "Пароль має містити від 8 до 20 символів"
        )
        String password

) {
}
