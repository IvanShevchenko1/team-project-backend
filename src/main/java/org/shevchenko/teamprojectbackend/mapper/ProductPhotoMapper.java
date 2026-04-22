package org.shevchenko.teamprojectbackend.mapper;

import java.time.Duration;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.dto.productPhoto.ProductPhotoResponseDto;
import org.shevchenko.teamprojectbackend.model.ProductPhoto;
import org.shevchenko.teamprojectbackend.service.ProductPhotoStorageService;

@Mapper(config = MapperConfig.class)
public interface ProductPhotoMapper {
    @Mapping(target = "url",
            source = "objectKey",
            qualifiedByName = "objectKeyToUrl")
    @Mapping(target = "isPrimary",
            source = "primary")
    ProductPhotoResponseDto toDto(ProductPhoto photo,
                                  @Context ProductPhotoStorageService productPhotoStorageService,
                                  @Context Duration presignDuration);

    @Named("firstPhotoToDto")
    default ProductPhotoResponseDto firstPhotoToDto(List<ProductPhoto> photos,
                                                   @Context ProductPhotoStorageService productPhotoStorageService,
                                                   @Context Duration presignDuration) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return toDto(photos.get(0), productPhotoStorageService, presignDuration);
    }

    @Named("objectKeyToUrl")
    default String objectKeyToUrl(String objectKey,
                                  @Context ProductPhotoStorageService productPhotoStorageService,
                                  @Context Duration presignDuration) {
        if (objectKey == null) {
            return null;
        }
        return productPhotoStorageService.generateViewUrl(objectKey, presignDuration);
    }
}
