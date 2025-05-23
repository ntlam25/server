package com.example.crabfood_api.dto.response;

import java.time.LocalTime;

import lombok.Data;

@Data
public class VendorResponse {
    private Long id;

    private Long userId;

    private String name;

    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private String cuisineType;

    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isActive;

    private double distance;
    private Double serviceRadiusKm;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private boolean isApproved;

    private Double rating;
    private Integer totalReviews;

    private Double minOrderAmount;
    private Double deliveryFee;
    private boolean isOpen;

    // Nếu cần trả về danh sách liên quan (ví dụ category, coupon, review...), có thể thêm DTO con
    // private List<CategorySimpleDTO> categories;
    // private List<CouponSimpleDTO> coupons;
    // private List<ReviewSimpleDTO> reviews;
}