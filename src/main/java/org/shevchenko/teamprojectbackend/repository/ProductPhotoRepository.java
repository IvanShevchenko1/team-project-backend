package org.shevchenko.teamprojectbackend.repository;

import org.shevchenko.teamprojectbackend.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
}
