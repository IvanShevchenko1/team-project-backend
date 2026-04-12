package org.shevchenko.teamprojectbackend.dto.user;

public record UpdateEmailResponseDto(
        Long id,
        String email,
        String name,
        String message
) {
}