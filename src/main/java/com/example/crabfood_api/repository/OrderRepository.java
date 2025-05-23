package com.example.crabfood_api.repository;

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
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId" +
            " AND o.orderStatus IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findOrdersByCustomerAndStatuses(@Param("customerId") Long customerId,
                                                @Param("statuses") List<OrderStatus> statuses);
}
