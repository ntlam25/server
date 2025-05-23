package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.enums.OrderTrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationUpdateResponse {
    private Long riderId;
    private Long orderId;
    private Double latitude;
    private Double longitude;
    private Double bearing;
    private Double speed;
    private LocalDateTime timestamp;
    private String estimatedArrivalTime;
    private int estimatedMinutes;
    private OrderTrackingStatus status;
}
