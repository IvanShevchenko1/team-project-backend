package org.shevchenko.teamprojectbackend.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // create isDeleted behaviour
    }

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
        if (lastMeaningfulActionAt == null) lastMeaningfulActionAt = now;
        if (previousVisitAt == null) previousVisitAt = now;
    }

    @PreUpdate
    void onUpdate() {
        if (lastActiveAt == null) lastActiveAt = LocalDateTime.now();
    }

    public User () {
    }

    public User(Long id) {
        this.id = id;
    }
}
