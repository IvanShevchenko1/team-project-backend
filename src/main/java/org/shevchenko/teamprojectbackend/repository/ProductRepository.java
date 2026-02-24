package org.shevchenko.teamprojectbackend.repository;

import org.shevchenko.teamprojectbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
