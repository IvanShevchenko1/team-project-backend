package org.shevchenko.teamprojectbackend.dto.product;

import java.time.LocalDateTime;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.model.Product;

public record ProductResponseDto(
        Long id,
        String title,
        String description,
        String city,
        Product.ProductStatus status,
        Long author,
        String category,
        String contact,
        ProductPhotoResponseDto image,
        LocalDateTime createdAt
) {
}
