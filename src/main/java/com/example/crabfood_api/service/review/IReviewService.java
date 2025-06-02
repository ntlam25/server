package com.example.crabfood_api.service.review;

import com.example.crabfood_api.dto.request.ReviewRequest;
import com.example.crabfood_api.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    ReviewResponse createReview(ReviewRequest request, Long userId);
    Page<ReviewResponse> getVendorReviews(Long vendorId, Pageable pageable);
    Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable);
    ReviewResponse getReviewByOrderId(Long orderId);
    Double getVendorAverageRating(Long vendorId);
    Long getVendorTotalReviews(Long vendorId);
}
