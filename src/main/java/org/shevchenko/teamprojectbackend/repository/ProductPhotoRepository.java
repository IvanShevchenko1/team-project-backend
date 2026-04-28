package org.shevchenko.teamprojectbackend.repository;

import java.util.Optional;
import org.shevchenko.teamprojectbackend.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
    Optional<ProductPhoto> findByProductId(Long productId);

    Optional<ProductPhoto> findByIdAndProductId(Long photoId, Long productId);
}
