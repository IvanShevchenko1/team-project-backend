package org.shevchenko.teamprojectbackend.repository;

import org.shevchenko.teamprojectbackend.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
}
