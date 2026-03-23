package org.shevchenko.teamprojectbackend.repository;

import java.util.Optional;
import org.shevchenko.teamprojectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByName(String name);
}
