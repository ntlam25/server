package com.example.crabfood_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GpsCoordinates {
    private Double latitude;
    private Double longitude;
}