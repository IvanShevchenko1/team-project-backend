package org.shevchenko.teamprojectbackend.service;

import jakarta.validation.Valid;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDto create(ProductCreateRequestDto request);

    Page<ProductResponseDto> getAll(Pageable pageable);

    Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable);

    void deleteById(Long id);

    ProductResponseDto updateById(Long id, ProductCreateRequestDto request);
}
