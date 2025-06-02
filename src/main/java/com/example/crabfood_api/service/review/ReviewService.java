package com.example.crabfood_api.service.review;

import com.example.crabfood_api.dto.request.ReviewRequest;
import com.example.crabfood_api.dto.response.FoodReviewResponse;
import com.example.crabfood_api.dto.response.ReviewResponse;
import com.example.crabfood_api.exception.BadRequestException;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.*;
import com.example.crabfood_api.model.enums.OrderStatus;
import com.example.crabfood_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService implements IReviewService{
    private final RiderProfileRepository riderProfileRepository;

    private final ReviewRepository reviewRepository;
    private final FoodReviewRepository foodReviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Long userId) {
        // Validate order exists and belongs to user
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(userId)) {
            throw new BadRequestException("You can only review your own orders");
        }

        // Check if order is completed
        if (!OrderStatus.SUCCESS.equals(order.getOrderStatus())) {
            throw new BadRequestException("You can only review completed orders");
        }

        // Check if review already exists
        if (reviewRepository.existsByOrderId(request.getOrderId())) {
            throw new BadRequestException("Order has already been reviewed");
        }

        // Get entities
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        RiderProfile rider = request.getRiderId() != null ?
                riderProfileRepository.findById(request.getRiderId()).orElse(null) : null;

        // Create review
        Review review = Review.builder()
                .order(order)
                .user(user)
                .vendor(vendor)
                .rider(rider)
                .foodRating(request.getFoodRating())
                .foodComment(request.getFoodComment())
                .deliveryRating(request.getDeliveryRating())
                .deliveryComment(request.getDeliveryComment())
                .images(request.getImages() != null ? String.join(",", request.getImages()) : null)
                .build();

        review = reviewRepository.save(review);

        // Create food reviews
        if (request.getFoodReviews() != null) {
            for (var foodReviewReq : request.getFoodReviews()) {
                Food food = foodRepository.findById(foodReviewReq.getFoodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Food not found: " + foodReviewReq.getFoodId()));

                FoodReview foodReview = FoodReview.builder()
                        .review(review)
                        .food(food)
                        .rating(foodReviewReq.getRating())
                        .comment(foodReviewReq.getComment())
                        .build();

                review.getFoodReviews().add(foodReview);
            }
        }

        review = reviewRepository.save(review);

        log.info("Created review for order: {}", request.getOrderId());
        return mapToResponse(review);
    }

    public Page<ReviewResponse> getVendorReviews(Long vendorId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByVendorIdOrderByCreatedAtDesc(vendorId, pageable);
        return reviews.map(this::mapToResponse);
    }

    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return reviews.map(this::mapToResponse);
    }

    public ReviewResponse getReviewByOrderId(Long orderId) {
        Review review = reviewRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for order: " + orderId));
        return mapToResponse(review);
    }

    public Double getVendorAverageRating(Long vendorId) {
        return reviewRepository.getAverageFoodRatingByVendorId(vendorId);
    }

    public Long getVendorTotalReviews(Long vendorId) {
        return reviewRepository.getTotalReviewsByVendorId(vendorId);
    }

    private ReviewResponse mapToResponse(Review review) {
        List<String> images = review.getImages() != null ?
                List.of(review.getImages().split(",")) : List.of();

        return ReviewResponse.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .orderCode(review.getOrder().getOrderNumber())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userAvatar(review.getUser().getAvatarUrl())
                .vendorId(review.getVendor().getId())
                .vendorName(review.getVendor().getName())
                .riderId(review.getRider() != null ? review.getRider().getId() : null)
                .riderName(review.getRider() != null ? review.getRider().getUser().getFullName() : null)
                .foodRating(review.getFoodRating())
                .foodComment(review.getFoodComment())
                .deliveryRating(review.getDeliveryRating())
                .deliveryComment(review.getDeliveryComment())
                .images(images)
                .createdAt(review.getCreatedAt())
                .foodReviews(review.getFoodReviews().stream()
                        .map(fr -> FoodReviewResponse.builder()
                                .id(fr.getId())
                                .foodId(fr.getFood().getId())
                                .foodName(fr.getFood().getName())
                                .foodImage(fr.getFood().getImageUrl())
                                .rating(fr.getRating())
                                .comment(fr.getComment())
                                .createdAt(fr.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
