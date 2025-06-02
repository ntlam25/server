// ReviewController.java
package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.ReviewRequest;
import com.example.crabfood_api.dto.response.ApiResponse;
import com.example.crabfood_api.dto.response.ReviewResponse;
import com.example.crabfood_api.service.review.IReviewService;
import com.example.crabfood_api.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;
    private final UserUtil userUtil;
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request) {

        Long userId = userUtil.getCurrentUser().getId();
        ReviewResponse response = reviewService.createReview(request, userId);

        return new ResponseEntity<>(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Create review successfully")
                .data(response).build(), HttpStatus.CREATED );
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getVendorReviews(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getVendorReviews(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .success(true)
                .message("Vendor reviews retrieved successfully")
                .data(reviews)
                .build());
    }

    @GetMapping("/user/my-reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = userUtil.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .success(true)
                .message("User reviews retrieved successfully")
                .data(reviews)
                .build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByOrderId(
            @PathVariable Long orderId) {

        ReviewResponse review = reviewService.getReviewByOrderId(orderId);

        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review retrieved successfully")
                .data(review)
                .build());
    }

    @GetMapping("/vendor/{vendorId}/stats")
    public ResponseEntity<ApiResponse<VendorReviewStats>> getVendorReviewStats(
            @PathVariable Long vendorId) {

        Double averageRating = reviewService.getVendorAverageRating(vendorId);
        Long totalReviews = reviewService.getVendorTotalReviews(vendorId);

        VendorReviewStats stats = VendorReviewStats.builder()
                .vendorId(vendorId)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews)
                .build();

        return ResponseEntity.ok(ApiResponse.<VendorReviewStats>builder()
                .success(true)
                .message("Vendor review stats retrieved successfully")
                .data(stats)
                .build());
    }

    // Inner class for vendor stats response
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class VendorReviewStats {
        private Long vendorId;
        private Double averageRating;
        private Long totalReviews;
    }
}