package com.example.crabfood_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crabfood_api.model.entity.OrderFood;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderFoodRepository extends JpaRepository<OrderFood, Long> {
    List<OrderFood> findByOrderId(Long orderId);
}
