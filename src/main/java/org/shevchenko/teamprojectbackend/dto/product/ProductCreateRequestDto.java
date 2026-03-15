package org.shevchenko.teamprojectbackend.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class ProductCreateRequestDto {

    @NotBlank(message = "Додайте назву товару")
    private String title;

    @NotBlank(message = "Додайте опис товару")
    private String description;

    @NotBlank(message = "Вкажіть місто")
    private String city;

    @NotBlank(message = "Додайте категорію")
    private String category;

    @NotBlank(message = "Вкажіть контактний номер телефону")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Некоректний номер телефону (10–15 цифр, може починатися з +)"
    )
    private String contactPhone;
}
