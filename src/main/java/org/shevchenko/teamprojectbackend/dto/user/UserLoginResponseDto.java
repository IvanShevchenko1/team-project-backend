package org.shevchenko.teamprojectbackend.dto.user;

public record UserLoginResponseDto(
        Long id,
        String email,
        String name,
        String token
) {
}
