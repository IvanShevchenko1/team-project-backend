package org.shevchenko.teamprojectbackend.mapper;

import java.time.Duration;
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
    ProductPhotoResponseDto toDto(ProductPhoto photo,
                                  @Context ProductPhotoStorageService productPhotoStorageService,
                                  @Context Duration presignDuration);

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
