package com.example.crabfood_api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VendorSearchRequest {
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @Positive(message = "Radius must be positive")
    private Double radius = 5.0;

    private String cuisineType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be >= 0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be <= 5")
    private Double minRating;

    @Min(value = 0, message = "Page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    private int size = 10;
}
