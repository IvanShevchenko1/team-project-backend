package org.shevchenko.teamprojectbackend.service.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.exception.EntityNotFoundException;
import org.shevchenko.teamprojectbackend.mapper.ProductMapper;
import org.shevchenko.teamprojectbackend.mapper.ProductPhotoMapper;
import org.shevchenko.teamprojectbackend.model.Product;
import org.shevchenko.teamprojectbackend.model.ProductPhoto;
import org.shevchenko.teamprojectbackend.model.User;
import org.shevchenko.teamprojectbackend.repository.ProductPhotoRepository;
import org.shevchenko.teamprojectbackend.repository.ProductRepository;
import org.shevchenko.teamprojectbackend.service.ProductPhotoStorageService;
import org.shevchenko.teamprojectbackend.service.ProductService;
import org.shevchenko.teamprojectbackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductPhotoRepository productPhotoRepository;
    private final ProductPhotoStorageService productPhotoStorageService;
    private final ProductPhotoMapper productPhotoMapper;
    private final UserService userService;

    @Value("${app.storage.presign-expiration-minutes:60}")
    private long presignExpirationMinutes;

    @Override
    @Transactional
    public ProductResponseDto create(ProductCreateRequestDto request, MultipartFile image) {
        Product product = productMapper.toModel(request);
        User currentUser = userService.getAuthenticatedUserOrThrow();
        product.setOwner(currentUser);
        Product savedProduct = productRepository.save(product);

        if (hasImage(image)) {
            ProductPhoto savedPhoto = savePhoto(savedProduct, image);
            savedProduct.setPhoto(savedPhoto);
        }

        return productMapper.toDto(savedProduct,
                productPhotoStorageService,
                presignDuration());
    }

    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> productMapper.toDto(product,
                        productPhotoStorageService,
                        presignDuration()));
    }

    @Override
    public Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable) {
        User user = userService.getAuthenticatedUserOrThrow();
        return productRepository.findAllByOwnerId(user.getId(), pageable)
                .map(product -> productMapper.toDto(product,
                        productPhotoStorageService,
                        presignDuration()));
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDto updateById(Long id, ProductCreateRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт за id " + id + " незнайдено."));
        productMapper.updateEntity(request, product);
        return productMapper.toDto(productRepository.save(product),
                productPhotoStorageService,
                presignDuration());
    }

    @Override
    @Transactional
    public ProductPhotoResponseDto uploadPhotos(Long productId, MultipartFile image) {
        validateSingleImageUploadRequest(image);

        Product product = getProductOrThrow(productId);
        User currentUser = userService.getAuthenticatedUserOrThrow();
        validateOwnership(product, currentUser);

        replaceExistingPhoto(productId);
        ProductPhoto savedPhoto = savePhoto(product, image);
        product.setPhoto(savedPhoto);

        return productPhotoMapper.toDto(savedPhoto,
                productPhotoStorageService,
                presignDuration());
    }

    @Override
    @Transactional
    public void deletePhoto(Long productId, Long photoId) {
        Product product = getProductOrThrow(productId);
        User currentUser = userService.getAuthenticatedUserOrThrow();
        validateOwnership(product, currentUser);

        ProductPhoto photo = productPhotoRepository.findByIdAndProductId(photoId, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Photo with id " + photoId + " was not found for product " + productId));

        productPhotoRepository.delete(photo);
        productPhotoStorageService.deleteObject(photo.getObjectKey());
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Продукт за id " + productId + " незнайдено."));
    }

    private ProductPhoto savePhoto(Product product, MultipartFile image) {
        ProductPhoto photo = new ProductPhoto();
        photo.setProduct(product);
        photo.setObjectKey(productPhotoStorageService.uploadProductPhoto(product.getId(), image));
        return productPhotoRepository.save(photo);
    }

    private void replaceExistingPhoto(Long productId) {
        productPhotoRepository.findByProductId(productId).ifPresent(existingPhoto -> {
            productPhotoStorageService.deleteObject(existingPhoto.getObjectKey());
            productPhotoRepository.delete(existingPhoto);
        });
    }

    private boolean hasImage(MultipartFile image) {
        return image != null && !image.isEmpty();
    }

    private Duration presignDuration() {
        return Duration.ofMinutes(presignExpirationMinutes);
    }

    private void validateOwnership(Product product, User currentUser) {
        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot modify this product");
        }
    }

    private void validateSingleImageUploadRequest(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("No image provided");
        }
    }
}
