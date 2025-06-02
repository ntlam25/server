package com.example.crabfood_api.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopFoodResponse {
    private Long foodId;
    private String foodName;
    private String description;
    private String imageUrl;
    private double price;
    private double rating;
    private String vendorName;
    private Long vendorId;
    private int totalOrdered;
    private double totalRevenue;
    private boolean isAvailable;
}