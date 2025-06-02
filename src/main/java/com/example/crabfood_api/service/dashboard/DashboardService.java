package com.example.crabfood_api.service.dashboard;

import com.example.crabfood_api.dto.response.*;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.model.enums.OrderStatus;
import com.example.crabfood_api.model.enums.Role;
import com.example.crabfood_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService{
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final FoodRepository foodRepository;

    @Override
    public DashboardStatsResponse getDashboardStats(Long vendorId) {
        return DashboardStatsResponse.builder()
                .orderStats(getOrderStats(vendorId))
                .restaurantStats(getRestaurantStats())
                .userStats(getUserStats())
                .revenueStats(getRevenueStats(vendorId))
                .build();
    }

    @Override
    public OrderStatsResponse getOrderStats(Long vendorId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime previousWeekStart = now.minusWeeks(2);

        if (vendorId != 0) {
            vendorRepository.findById(vendorId).orElseThrow(() ->
                    new ResourceNotFoundException("Vendor not found"));
        } else {
            vendorId = null;
        }

        // Current week stats
        long total = vendorId == null ? orderRepository.count() : orderRepository.countByVendorId(vendorId);
        long pending = orderRepository.countByOrderStatus(OrderStatus.PENDING, vendorId);
        long processing = orderRepository.countByOrderStatus(OrderStatus.ACCEPTED, vendorId)
                + orderRepository.countByOrderStatus(OrderStatus.PICKED_UP, vendorId)
                + orderRepository.countByOrderStatus(OrderStatus.ON_THE_WAY, vendorId);
        long completed = orderRepository.countByOrderStatus(OrderStatus.SUCCESS, vendorId);
        long cancelled = orderRepository.countByOrderStatus(OrderStatus.CANCELLED, vendorId);

        // Weekly growth calculation
        long currentWeekOrders = orderRepository.countByCreatedAtBetween(weekStart, now);
        long previousWeekOrders = orderRepository.countByCreatedAtBetween(previousWeekStart, weekStart);

        double weeklyGrowthPercent = calculateGrowthPercent(currentWeekOrders, previousWeekOrders);

        return OrderStatsResponse.builder()
                .total(total)
                .pending(pending)
                .processing(processing)
                .completed(completed)
                .cancelled(cancelled)
                .weeklyGrowthPercent(weeklyGrowthPercent)
                .build();
    }

    @Override
    public RestaurantStatsResponse getRestaurantStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime previousMonthStart = now.minusMonths(2);

        long total = vendorRepository.count();
        long active = vendorRepository.countByIsActiveTrue();
        long inactive = vendorRepository.countByIsActiveFalse();

        // Monthly growth calculation
        long currentMonthVendors = vendorRepository.countByCreatedAtBetween(monthStart, now);
        long previousMonthVendors = vendorRepository.countByCreatedAtBetween(previousMonthStart, monthStart);

        double monthlyGrowthPercent = calculateGrowthPercent(currentMonthVendors, previousMonthVendors);

        return RestaurantStatsResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .monthlyGrowthPercent(monthlyGrowthPercent)
                .build();
    }

    @Override
    public UserStatsResponse getUserStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime previousMonthStart = now.minusMonths(2);

        long total = userRepository.count();
        long customers = userRoleRepository.countByRole(Role.CUSTOMER);
        long vendors = userRoleRepository.countByRole(Role.VENDOR);
        long admins = userRoleRepository.countByRole(Role.ADMIN);

        // Monthly growth calculation
        long currentMonthUsers = userRepository.countByCreatedAtBetween(monthStart, now);
        long previousMonthUsers = userRepository.countByCreatedAtBetween(previousMonthStart, monthStart);

        double monthlyGrowthPercent = calculateGrowthPercent(currentMonthUsers, previousMonthUsers);

        return UserStatsResponse.builder()
                .total(total)
                .customers(customers)
                .vendors(vendors)
                .admins(admins)
                .monthlyGrowthPercent(monthlyGrowthPercent)
                .build();
    }

    @Override
    public RevenueStatsResponse getRevenueStats(Long vendorId) {
        LocalDateTime now = LocalDateTime.now();

        // Define time periods
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime yearStart = now.minusYears(1);
        LocalDateTime previousMonthStart = now.minusMonths(2);

        if (vendorId != 0) {
            vendorRepository.findById(vendorId).orElseThrow(() ->
                    new ResourceNotFoundException("Vendor not found"));
        } else {
            vendorId = null;
        }

        // Calculate revenues for different periods
        double todayRevenue = orderRepository.getTotalRevenueByDateRange(todayStart, now, vendorId);
        double weekRevenue = orderRepository.getTotalRevenueByDateRange(weekStart, now, vendorId);
        double monthRevenue = orderRepository.getTotalRevenueByDateRange(monthStart, now, vendorId);
        double yearRevenue = orderRepository.getTotalRevenueByDateRange(yearStart, now, vendorId);

        // Previous month revenue for growth calculation
        double previousMonthRevenue = orderRepository.getTotalRevenueByDateRange(previousMonthStart, monthStart, vendorId);
        double monthlyGrowthPercent = calculateGrowthPercent(monthRevenue, previousMonthRevenue);

        // Average order value and total orders
        long totalOrdersThisMonth = orderRepository.countByCreatedAtBetween(monthStart, now);
        double averageOrderValue = totalOrdersThisMonth > 0 ? monthRevenue / totalOrdersThisMonth : 0;

        return RevenueStatsResponse.builder()
                .today(todayRevenue) // Convert to thousands
                .thisWeek(weekRevenue)
                .thisMonth(monthRevenue)
                .thisYear(yearRevenue) // Convert to millions
                .monthlyGrowthPercent(monthlyGrowthPercent)
                .averageOrderValue(averageOrderValue)
                .totalOrders(totalOrdersThisMonth)
                .build();
    }

    @Override
    public List<RevenueTrendResponse> getRevenueTrend(int days, Long vendorId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        if (vendorId != 0) {
            vendorRepository.findById(vendorId).orElseThrow(() ->
                    new ResourceNotFoundException("Vendor not found"));
        } else {
            vendorId = null;
        }

        return orderRepository.getDailyRevenueTrend(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), vendorId)
                .stream()
                .map(result -> RevenueTrendResponse.builder()
                        .date(result[0].toString())
                        .revenue(((BigDecimal) result[1]).doubleValue())
                        .orderCount(((Number) result[2]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<TopFoodResponse> getTopFoods(int limit, String period, Long vendorId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(period, endDate);

        Pageable pageable = PageRequest.of(0, limit);

        if (vendorId != null && vendorId > 0) {
            return foodRepository.findTopFoodsByVendorAndDateRange(vendorId, startDate, endDate, OrderStatus.CANCELLED, pageable);
        } else {
            return foodRepository.findTopFoodsByDateRange(startDate, endDate, OrderStatus.CANCELLED, pageable);
        }
    }

    public List<TopFoodResponse> getTopFoodsByCustomDateRange(
            int limit,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long vendorId) {

        Pageable pageable = PageRequest.of(0, limit);

        if (vendorId != null && vendorId > 0) {
            return foodRepository.findTopFoodsByVendorAndDateRange(vendorId, startDate, endDate, OrderStatus.CANCELLED, pageable);
        } else {
            return foodRepository.findTopFoodsByDateRange(startDate, endDate, OrderStatus.CANCELLED, pageable);
        }
    }

    private LocalDateTime calculateStartDate(String period, LocalDateTime endDate) {
        return switch (period.toLowerCase()) {
            case "today" -> endDate.toLocalDate().atStartOfDay();
            case "week" -> endDate.minusWeeks(1);
            case "month" -> endDate.minusMonths(1);
            case "quarter" -> endDate.minusMonths(3);
            case "year" -> endDate.minusYears(1);
            default -> endDate.minusWeeks(1); // default to week
        };
    }

    private double calculateGrowthPercent(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100.0;
    }
}
