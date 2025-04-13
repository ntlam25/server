package com.example.crabfood_api.model.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private String restaurantName;
    
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String cuisineType;
    
    @Column(nullable = false)
    private String address;
    
    @Column(columnDefinition = "POINT")
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
    private double minOrderAmount = 0.0;
    @Builder.Default
    private double deliveryFee = 0.0;
    @Builder.Default
    private boolean isOpen = true;

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

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