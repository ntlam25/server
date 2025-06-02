package com.example.crabfood_api.model.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vendors")
public class Vendor extends MasterDataBaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;
    private String logoUrl;
    private String coverImageUrl;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    @Builder.Default
    private double serviceRadiusKm = 5.0;

    private LocalTime openingTime;
    private LocalTime closingTime;

    @Builder.Default
    private boolean isApproved = false;

    @Builder.Default
    private double rating = 0.0;
    @Builder.Default
    private int totalReviews = 0;
    @Builder.Default
    private double deliveryFee = 0.0;
    @Builder.Default
    private boolean isOpen = true;
    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private boolean isFavorite = false;

    private String cuisineType;

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Food> foods = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coupon> coupons = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}