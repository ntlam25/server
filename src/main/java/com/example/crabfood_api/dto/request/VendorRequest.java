package com.example.crabfood_api.dto.request;

import java.time.LocalTime;

import lombok.Data;

@Data
public class VendorRequest {
    private Long userId; 

    private String name;

    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String cuisineType;

    private String address;

    private Double latitude;
    private Double longitude;

    private double serviceRadiusKm;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private boolean isApproved = false;

    private Double minOrderAmount;
    private Double deliveryFee;
    private boolean isActive;
    private boolean isOpen;
}
