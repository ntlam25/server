package com.example.crabfood_api.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReviewResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private String foodImage;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
