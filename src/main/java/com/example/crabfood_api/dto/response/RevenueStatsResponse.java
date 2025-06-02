package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsResponse {
    private double today;
    private double thisWeek;
    private double thisMonth;
    private double thisYear;
    private double monthlyGrowthPercent;
    private double averageOrderValue;
    private long totalOrders;
}
