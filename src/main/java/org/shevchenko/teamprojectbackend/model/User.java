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
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String userCity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastActiveAt;

    private LocalDateTime lastMeaningfulActionAt;

    private int totalProductsGiven;

    private int totalProductsReceived;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
        if (lastActiveAt == null) lastActiveAt = now;
    }

    @PreUpdate
    void onUpdate() {
        if (lastActiveAt == null) lastActiveAt = LocalDateTime.now();
    }

}
