package org.shevchenko.teamprojectbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String userCity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastMeaningfulActionAt;

    private int totalProductsGiven;

    private int totalProductsReceived;

    private UserStatus accountStatus;

    private LocalDateTime previousVisitAt;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public enum UserStatus {
        active,
        suspended,
        deleted
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

}
