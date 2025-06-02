package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.crabfood_api.model.enums.RiderStatus;

import jakarta.persistence.*;
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
@Table(name = "riderProfiles")
public class RiderProfile extends MasterDataBaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String vehicleType;
    private String vehicleNumber;
    private String idNumber;
    
    @Enumerated(EnumType.STRING)
    private RiderStatus status;

    private Double currentLatitude;
    private Double currentLongitude;

    @Builder.Default
    private int totalDeliveries = 0;

    @Builder.Default
    private double rating = 5.0;

    @Builder.Default
    private int totalRatings = 0;

    @Builder.Default
    private boolean onlineStatus = false;

    private LocalDateTime lastActive;

    @Transient
    private double priorityScore;

    @Builder.Default
    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> riderOrders = new ArrayList<>();
}