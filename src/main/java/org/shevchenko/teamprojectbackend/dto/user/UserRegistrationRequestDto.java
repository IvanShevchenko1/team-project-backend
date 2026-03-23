package org.shevchenko.teamprojectbackend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequestDto {

    @NotBlank(message = "Вкажіть електронну пошту")
    @Email(message = "Некоректний формат електронної пошти")
    private String email;

    @NotBlank(message = "Вкажіть пароль")
    @Size(
            min = 8,
            max = 20,
            message = "Пароль має містити від 8 до 20 символів"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_\\-]).*$",
            message = "Пароль має містити велику літеру, малу літеру, цифру та спеціальний символ"
    )
    private String password;

    @NotBlank(message = "Вкажіть ім'я")
    @Size(min = 3, max = 12, message = "Ім'я має бути від 3 до 12 символів")
    @Pattern(
            regexp = "^[^\\s].*[^\\s]$",
            message = "Ім'я не повинно починатися або закінчуватися пробілами"
    )
    private String name;
}
