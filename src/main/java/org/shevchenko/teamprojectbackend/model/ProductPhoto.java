package org.shevchenko.teamprojectbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "product_photos")
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String photoUrl;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(nullable = false)
    private int displayOrder = 0;

    @PrePersist
    void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }
}
