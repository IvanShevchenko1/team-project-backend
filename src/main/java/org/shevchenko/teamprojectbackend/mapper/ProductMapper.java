package org.shevchenko.teamprojectbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.dto.product.ProductCreateRequestDto;
import org.shevchenko.teamprojectbackend.dto.product.ProductResponseDto;
import org.shevchenko.teamprojectbackend.model.Product;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class, CategoryMapper.class})
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "ownerId",
            source = "owner",
            qualifiedByName = "userToId"),
            @Mapping(target = "categoryId",
            source = "category",
            qualifiedByName = "categoryToId")
    })
    ProductResponseDto toDto(Product product);

    @Mappings({
            @Mapping(target = "category",
            source = "categoryId",
            qualifiedByName = "categoryFromId"),
            @Mapping(target = "owner", ignore = true)
    })
    Product toModel(ProductCreateRequestDto request);
}
