package org.shevchenko.teamprojectbackend.mapper;

import org.mapstruct.*;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.model.Product;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class})
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "author",
            source = "owner",
            qualifiedByName = "userToId"),
            @Mapping(target = "contact",
            source = "contactPhone")
    })
    ProductResponseDto toDto(Product product);

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
