package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;

import com.example.crabfood_api.model.enums.RiderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "riderProfiles")
public class RiderProfile extends MasterDataBaseEntity {

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

}