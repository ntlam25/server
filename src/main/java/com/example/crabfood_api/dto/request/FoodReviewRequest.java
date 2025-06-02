package com.example.crabfood_api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReviewRequest {
    @NotNull
    private Long foodId;

    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}

