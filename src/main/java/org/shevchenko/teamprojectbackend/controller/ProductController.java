package org.shevchenko.teamprojectbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product listing, creation, updates, and photos")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(value = "/createProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a product",
            description = "Creates a new product for the authenticated user with an optional single image",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data or image request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ProductResponseDto create(
            @ModelAttribute @Valid ProductCreateRequestDto request,
            @Parameter(description = "Optional image file. Only one image is allowed.")
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "Optional image file. Alternative to files.")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.create(request, resolveSingleImage(files, image));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get public products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    public Page<ProductResponseDto> getAll(
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return productService.getAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/myProducts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get products owned by the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable) {
        return productService.getAllForCurrentUser(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a product",
            description = "Soft-deletes a product by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Product was not found")
    })
    public void deleteById(@Parameter(description = "Product id") @PathVariable Long id) {
        productService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a product",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Product was not found")
    })
    public ProductResponseDto updateProduct(@Parameter(description = "Product id") @PathVariable Long id,
                                            @RequestBody @Valid ProductCreateRequestDto request) {
        return productService.updateById(id, request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Upload or replace a product photo",
            description = "Uploads one image for a product owned by the authenticated user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Photo uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "No image or more than one image provided"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Current user does not own the product"),
            @ApiResponse(responseCode = "404", description = "Product was not found")
    })
    public ProductPhotoResponseDto uploadPhotos(
            @Parameter(description = "Product id") @PathVariable Long id,
            @Parameter(description = "Image file. Only one image is allowed.")
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "Image file. Alternative to files.")
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.uploadPhotos(id, resolveSingleImage(files, image));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{productId}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a product photo",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Photo deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Current user does not own the product"),
            @ApiResponse(responseCode = "404", description = "Product or photo was not found")
    })
    public void deletePhoto(@Parameter(description = "Product id") @PathVariable Long productId,
                            @Parameter(description = "Photo id") @PathVariable Long photoId) {
        productService.deletePhoto(productId, photoId);
    }

    private MultipartFile resolveSingleImage(List<MultipartFile> files, MultipartFile image) {
        MultipartFile fileFromList = null;
        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    if (fileFromList != null) {
                        throw new IllegalArgumentException("Only one image is allowed");
                    }
                    fileFromList = file;
                }
            }
        }

        if (fileFromList != null && image != null && !image.isEmpty()) {
            throw new IllegalArgumentException("Only one image is allowed");
        }

        if (image != null && !image.isEmpty()) {
            return image;
        }
        return fileFromList;
    }
}
