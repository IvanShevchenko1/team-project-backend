package org.shevchenko.teamprojectbackend.dto.product;

import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.model.Product;
import java.util.List;

public record ProductResponseDto(
        Long id,
        String title,
        String description,
        String city,
        Product.ProductStatus status,
        Long author,
        String category,
        String contact,
        List<ProductPhotoResponseDto> image
) {
}
