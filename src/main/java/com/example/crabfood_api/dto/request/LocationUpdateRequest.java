package com.example.crabfood_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationUpdateRequest {
    private Long riderId;
    private Long orderId;
    private Double latitude;
    private Double longitude;
    private Double bearing; // direction in degrees
    private Double speed; // in m/s
}

