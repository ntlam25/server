package com.example.crabfood_api.dto.response;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FoodResponse {
    private Long id;
    private Long vendorId;
    private String name;
    private String description;
    private double price;
    private List<SimpleCategoryResponse> categories;
    private String imageUrl;
    private boolean isAvailable;
    private int totalReviews;
    private int preparationTime;
    private boolean isFeatured;
    private double rating;

    private List<FoodOptionResponse> options;
}