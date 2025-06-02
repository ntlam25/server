package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "foods")
public class Food extends BaseEntity {

    @ManyToMany
    @JoinTable(
            name = "category_food",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double price;

    private String imageUrl;
    @Builder.Default
    private boolean isAvailable = true;

    @Builder.Default
    private boolean isFeatured = false;

    @Builder.Default
    private boolean isFavorite = false;

    private int preparationTime;

    @Column(columnDefinition = "DECIMAL(2,1)")
    @Builder.Default
    private double rating = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodOption> options = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY)
    private List<OrderFood> orderFoods = new ArrayList<>();
}
