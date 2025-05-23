package com.example.crabfood_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorSearchRequest {
    private String keyword;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private Integer limit;
    private Integer offset;
    private String cuisineType;
    private Double minRating;
    private Boolean isOpen;
    private String sortBy;
}
