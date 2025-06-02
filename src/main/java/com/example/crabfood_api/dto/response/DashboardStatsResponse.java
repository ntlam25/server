package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private OrderStatsResponse orderStats;
    private RestaurantStatsResponse restaurantStats;
    private UserStatsResponse userStats;
    private RevenueStatsResponse revenueStats;
}

