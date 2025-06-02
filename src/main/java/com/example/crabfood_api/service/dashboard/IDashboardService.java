package com.example.crabfood_api.service.dashboard;

import com.example.crabfood_api.dto.response.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IDashboardService {
    DashboardStatsResponse getDashboardStats(Long vendorId);

    OrderStatsResponse getOrderStats(Long vendorId);

    RestaurantStatsResponse getRestaurantStats();

    UserStatsResponse getUserStats();

    RevenueStatsResponse getRevenueStats(Long vendorId);

    List<RevenueTrendResponse> getRevenueTrend(int days, Long vendorId);

    List<TopFoodResponse> getTopFoods(int limit, String period, Long vendorId);

    List<TopFoodResponse> getTopFoodsByCustomDateRange(
            int limit,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long vendorId);
}
