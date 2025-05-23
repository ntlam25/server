package com.example.crabfood_api.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String slug;
    private int displayOrder;
    private boolean isActive;
    private boolean isGlobal;
    private List<FoodResponse> foods;
}
