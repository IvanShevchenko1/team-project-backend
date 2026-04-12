package org.shevchenko.teamprojectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/createProduct")
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

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto updateProduct(@PathVariable Long id,
                                            @RequestBody @Valid ProductCreateRequestDto request) {
        return productService.updateById(id, request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductPhotoResponseDto> uploadPhotos(
            @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files) {
        return productService.uploadPhotos(id, files);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{productId}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(@PathVariable Long productId, @PathVariable Long photoId) {
        productService.deletePhoto(productId, photoId);
    }
}
