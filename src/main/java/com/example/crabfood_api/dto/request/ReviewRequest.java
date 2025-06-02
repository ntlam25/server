package com.example.crabfood_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    private Long orderId;

    @NotNull
    private Long vendorId;

    private Long riderId;

    @Min(1)
    @Max(5)
    private Double foodRating;

    private String foodComment;

    @Min(1)
    @Max(5)
    private Double deliveryRating;

    private String deliveryComment;

    private List<String> images;

    @Valid
    private List<FoodReviewRequest> foodReviews;
}

