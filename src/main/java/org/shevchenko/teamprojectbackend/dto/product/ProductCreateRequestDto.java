package org.shevchenko.teamprojectbackend.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String city;
    @NotNull
    private Long categoryId;
}
