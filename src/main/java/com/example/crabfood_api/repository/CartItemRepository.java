package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndFoodId(Long userId, Long foodId);
    void deleteByUserId(Long userId);
}