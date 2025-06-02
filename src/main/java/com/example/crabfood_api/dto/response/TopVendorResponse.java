package com.example.crabfood_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopVendorResponse {
    private Long id;
    private String name;
    private String logoUrl;
    private String cuisineType;
    private double rating;
    private int totalReviews;
    private long totalOrders;
    private double completionRate;
    private double deliveryFee;
    private boolean isOpen;
    private String address;
}
