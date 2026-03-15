package org.shevchenko.teamprojectbackend.dto.product;

import org.shevchenko.teamprojectbackend.model.Product;

public record ProductResponseDto(
        Long id,
        String title,
        String description,
        String city,
        Product.ProductStatus status,
        Long author,
        String category,
        String contact
) {
}
