package org.shevchenko.teamprojectbackend.repository;

import org.shevchenko.teamprojectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
