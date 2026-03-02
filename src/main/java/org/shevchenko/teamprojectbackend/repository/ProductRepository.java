package org.shevchenko.teamprojectbackend.repository;

import org.shevchenko.teamprojectbackend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByOwnerId(Long ownerId, Pageable pageable);
}
