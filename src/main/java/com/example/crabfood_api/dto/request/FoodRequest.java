package com.example.crabfood_api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class FoodRequest {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private boolean isAvailable;
    private int preparationTime;
    private boolean isFeatured;
    private Long vendorId;              // ID của vendor liên kết
    private List<Long> categoryIds;
    private List<FoodOptionRequest> options;
}
