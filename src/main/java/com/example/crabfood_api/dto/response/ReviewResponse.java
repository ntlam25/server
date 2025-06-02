package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long orderId;
    private String orderCode;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long vendorId;
    private String vendorName;
    private Long riderId;
    private String riderName;
    private Double foodRating;
    private String foodComment;
    private Double deliveryRating;
    private String deliveryComment;
    private List<String> images;
    private LocalDateTime createdAt;
    private List<FoodReviewResponse> foodReviews;
}