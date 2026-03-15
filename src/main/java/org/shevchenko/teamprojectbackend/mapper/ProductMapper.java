package org.shevchenko.teamprojectbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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
    })
    ProductResponseDto toDto(Product product);

    @Mappings({
            @Mapping(target = "owner", ignore = true)
    })
    Product toModel(ProductCreateRequestDto request);
}
