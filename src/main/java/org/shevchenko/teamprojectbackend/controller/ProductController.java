package org.shevchenko.teamprojectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @RequestBody @Valid ProductCreateRequestDto request) {
        return productService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/myProducts")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable) {
        return productService.getAllForCurrentUser(pageable);
    }
}
