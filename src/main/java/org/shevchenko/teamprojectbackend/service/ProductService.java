package org.shevchenko.teamprojectbackend.service;

import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    ProductResponseDto create(ProductCreateRequestDto request, List<MultipartFile> files);

    Page<ProductResponseDto> getAll(Pageable pageable);

    Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable);

    void deleteById(Long id);

    ProductResponseDto updateById(Long id, ProductCreateRequestDto request);

    List<ProductPhotoResponseDto> uploadPhotos(Long productId, List<MultipartFile> files);

    void deletePhoto(Long productId, Long photoId);

}
