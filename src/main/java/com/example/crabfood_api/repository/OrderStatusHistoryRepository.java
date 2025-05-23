package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
}
