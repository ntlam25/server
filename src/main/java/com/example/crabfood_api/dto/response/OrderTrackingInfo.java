package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.enums.OrderTrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrackingInfo {
    private Long orderId;
    private Long riderId;
    private String riderName;
    private Double riderRating;
    private String riderImageUrl;
    private OrderTrackingStatus status;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    private String estimatedDeliveryTime;
    private String restaurantName;
}