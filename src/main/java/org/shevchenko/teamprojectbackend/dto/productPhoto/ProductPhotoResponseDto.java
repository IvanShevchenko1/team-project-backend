package org.shevchenko.teamprojectbackend.dto.productPhoto;

public record ProductPhotoResponseDto(
        Long id,
        String url,
        boolean isPrimary,
        int displayOrder
) {
}