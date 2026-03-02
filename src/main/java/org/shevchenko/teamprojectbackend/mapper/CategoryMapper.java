package org.shevchenko.teamprojectbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.shevchenko.teamprojectbackend.config.MapperConfig;
import org.shevchenko.teamprojectbackend.model.Categories;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {

    @Named("categoryToId")
    default Long getCategoryId(Categories category) {
        return category.getId();
    }

    @Named("categoryFromId")
    default Categories categoryFromId(Long id) {
        return new Categories(id);
    }
}
