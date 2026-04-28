package org.shevchenko.teamprojectbackend.mapper;

import java.time.Duration;
import org.mapstruct.*;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.model.Product;
import org.shevchenko.teamprojectbackend.service.ProductPhotoStorageService;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class, ProductPhotoMapper.class})
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "author",
            source = "owner",
            qualifiedByName = "userToId"),
            @Mapping(target = "contact",
            source = "contactPhone"),
            @Mapping(target = "image",
            source = "photo")
    })
    ProductResponseDto toDto(Product product,
                             @Context ProductPhotoStorageService productPhotoStorageService,
                             @Context Duration presignDuration);

    @Mappings({
            @Mapping(target = "owner", ignore = true),
            @Mapping(target = "contactPhone",
                    source = "contact")
    })
    Product toModel(ProductCreateRequestDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "contactPhone",
            source = "contact")
    void updateEntity(ProductCreateRequestDto request, @MappingTarget Product product);
}
