package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantStatsResponse {
    private long total;
    private long active;
    private long inactive;
    private double monthlyGrowthPercent;
}
