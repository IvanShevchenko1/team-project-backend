package org.shevchenko.teamprojectbackend.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
    public ProductResponseDto create(ProductCreateRequestDto request, List<MultipartFile> files) {
        Product product = productMapper.toModel(request);
        User currentUser = userService.getAuthenticatedUserOrThrow();
        product.setOwner(currentUser);
        Product savedProduct = productRepository.save(product);

        if (hasFiles(files)) {
            validatePhotoUploadRequest(files);
            List<ProductPhoto> savedPhotos = savePhotos(savedProduct, files, 0);
            savedProduct.getPhotos().addAll(savedPhotos);
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
    public List<ProductPhotoResponseDto> uploadPhotos(Long productId, List<MultipartFile> files) {
        validatePhotoUploadRequest(files);

        Product product = getProductOrThrow(productId);
        User currentUser = userService.getAuthenticatedUserOrThrow();
        validateOwnership(product, currentUser);

        long existingCount = productPhotoRepository.countByProductId(productId);
        validatePhotoLimit(productId, files.size());

        return savePhotos(product, files, existingCount).stream()
                .map(photo -> productPhotoMapper.toDto(photo,
                        productPhotoStorageService,
                        presignDuration()))
                .toList();
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

    private List<ProductPhoto> savePhotos(Product product, List<MultipartFile> files, long existingCount) {
        List<ProductPhoto> photos = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            ProductPhoto photo = new ProductPhoto();
            photo.setProduct(product);
            photo.setObjectKey(productPhotoStorageService.uploadProductPhoto(product.getId(), files.get(i)));
            photo.setPrimary(existingCount == 0 && i == 0);
            photo.setDisplayOrder((int) existingCount + i);
            photos.add(photo);
        }
        return productPhotoRepository.saveAll(photos);
    }

    private boolean hasFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty();
    }

    private Duration presignDuration() {
        return Duration.ofMinutes(presignExpirationMinutes);
    }

    private void validateOwnership(Product product, User currentUser) {
        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot modify this product");
        }
    }

    private void validatePhotoUploadRequest(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided");
        }

        if (files.size() > 10) {
            throw new IllegalArgumentException("You can upload at most 10 photos at once");
        }
    }

    private void validatePhotoLimit(Long productId, int incomingCount) {
        long existingCount = productPhotoRepository.countByProductId(productId);
        if (existingCount + incomingCount > 10) {
            throw new IllegalArgumentException("A product can have at most 10 photos");
        }
    }
}
