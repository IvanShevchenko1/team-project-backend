package org.shevchenko.teamprojectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.mapper.ProductMapper;
import org.shevchenko.teamprojectbackend.model.Product;
import org.shevchenko.teamprojectbackend.model.User;
import org.shevchenko.teamprojectbackend.repository.ProductRepository;
import org.shevchenko.teamprojectbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto create(ProductCreateRequestDto request) {
        Product product = productMapper.toModel(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        product.setOwner(currentUser);
        return productMapper.toDto(productRepository
                .save(product));
    }

    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponseDto> getAllForCurrentUser(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return productRepository.findAllByOwnerId(user.getId(), pageable)
                .map(productMapper::toDto);
    }
}
