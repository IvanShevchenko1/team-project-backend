package org.shevchenko.teamprojectbackend.service;

import java.time.Duration;
import org.springframework.web.multipart.MultipartFile;

public interface ProductPhotoStorageService {
    String uploadProductPhoto(Long productId, MultipartFile file);

    String generateViewUrl(String objectKey, Duration duration);

    void deleteObject(String objectKey);
}
