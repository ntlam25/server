package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackingInfo {
    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private Double riderLatitude;
    private Double riderLongitude;
    private String riderName;
    private String riderPhone;
    private LocalDateTime estimatedDeliveryTime;
    private String currentLocation;
    private String nextLocation;
}