package org.shevchenko.teamprojectbackend.dto.user;

public record UserResponseDto(
        Long id,
        String email,
        String name,
        String message
) {
}
