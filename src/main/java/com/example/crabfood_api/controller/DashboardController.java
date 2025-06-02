package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.response.*;
import com.example.crabfood_api.service.dashboard.IDashboardService;
import com.example.crabfood_api.service.vendor.IVendorService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final IDashboardService dashboardStatsService;
    private final IVendorService vendorService;

    public DashboardController(IVendorService vendorService, IDashboardService dashboardStatsService) {
        this.vendorService = vendorService;
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<TopVendorResponse>>> getTopVendors(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TopVendorResponse> topVendors = vendorService.getTopVendors(limit);

            ApiResponse<List<TopVendorResponse>> response = ApiResponse.<List<TopVendorResponse>>builder()
                    .success(true)
                    .message("Top vendors retrieved successfully")
                    .data(topVendors)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<TopVendorResponse>> response = ApiResponse.<List<TopVendorResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve top vendors: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top-restaurants")
    public ResponseEntity<ApiResponse<List<TopVendorResponse>>> getTopRestaurants() {
        try {
            // Get top 4 vendors for the main display
            List<TopVendorResponse> topRestaurants = vendorService.getTopVendors(4);

            ApiResponse<List<TopVendorResponse>> response = ApiResponse.<List<TopVendorResponse>>builder()
                    .success(true)
                    .message("Top restaurants retrieved successfully")
                    .data(topRestaurants)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<TopVendorResponse>> response = ApiResponse.<List<TopVendorResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve top restaurants: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(@RequestParam Long vendorId) {
        return ResponseEntity.ok(dashboardStatsService.getDashboardStats(vendorId));
    }

    @GetMapping("/stats/orders")
    public ResponseEntity<OrderStatsResponse> getOrderStats(@RequestParam Long vendorId) {
        return ResponseEntity.ok(dashboardStatsService.getOrderStats(vendorId));
    }

    @GetMapping("/stats/restaurants")
    public ResponseEntity<RestaurantStatsResponse> getRestaurantStats() {
        return ResponseEntity.ok(dashboardStatsService.getRestaurantStats());
    }

    @GetMapping("/stats/users")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(dashboardStatsService.getUserStats());
    }

    @GetMapping("/stats/revenue")
    public ResponseEntity<RevenueStatsResponse> getRevenueStats(@RequestParam Long vendorId) {
        return ResponseEntity.ok(dashboardStatsService.getRevenueStats(vendorId));
    }

    @GetMapping("/revenue-trend")
    public ResponseEntity<List<RevenueTrendResponse>> getRevenueTrend(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam Long vendorId) {
        return ResponseEntity.ok(dashboardStatsService.getRevenueTrend(days, vendorId));
    }

    @GetMapping("/top-foods")
    public ResponseEntity<List<TopFoodResponse>> getTopFoods(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(required = false) Long vendorId) {

        List<TopFoodResponse> topFoods = dashboardStatsService.getTopFoods(limit, period, vendorId);
        return ResponseEntity.ok(topFoods);
    }

    @GetMapping("/top-foods/custom-range")
    public ResponseEntity<List<TopFoodResponse>> getTopFoodsByCustomRange(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam(required = false) Long vendorId) {

        List<TopFoodResponse> topFoods = dashboardStatsService.getTopFoodsByCustomDateRange(
                limit, startDate, endDate, vendorId);
        return ResponseEntity.ok(topFoods);
    }
}
