package com.example.crabfood_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.crabfood_api.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crabfood_api.model.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface  OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByVendorId(Long vendorId);
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query(value = "SELECT o FROM Order o WHERE o.customer.id = :customerId" +
            " AND o.orderStatus IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findOrdersByCustomerAndStatuses(@Param("customerId") Long customerId,
                                                @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o.id) FROM Order o WHERE (:vendorId IS NULL OR o.vendor.id = :vendorId) " +
            "AND o.orderStatus = :status")
    long countByOrderStatus(OrderStatus status, Long vendorId);

    long countByVendorId(Long vendorId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o" +
            " WHERE o.createdAt BETWEEN :start AND :end " +
            "AND o.orderStatus = 'SUCCESS' AND (:vendorId IS NULL OR o.vendor.id = :vendorId)")
    double getTotalRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Long vendorId);

    @Query("SELECT DATE(o.createdAt), COALESCE(SUM(o.totalAmount), 0), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.createdAt BETWEEN :start AND :end AND o.orderStatus = 'SUCCESS'" +
            " AND (:vendorId IS NULL OR o.vendor.id = :vendorId) " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailyRevenueTrend(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Long vendorId);
}
