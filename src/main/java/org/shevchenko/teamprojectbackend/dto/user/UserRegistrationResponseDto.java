package org.shevchenko.teamprojectbackend.dto.user;

public record UserRegistrationResponseDto(
        Long id,
        String email,
        String name,
        String message,
        String userCity,
        String token
) {
}
