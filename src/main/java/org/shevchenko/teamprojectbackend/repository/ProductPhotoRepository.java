package org.shevchenko.teamprojectbackend.repository;

import java.util.List;
import java.util.Optional;
import org.shevchenko.teamprojectbackend.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
    List<ProductPhoto> findAllByProductIdOrderByDisplayOrderAsc(Long productId);

    Optional<ProductPhoto> findByIdAndProductId(Long photoId, Long productId);

    long countByProductId(Long productId);
}
