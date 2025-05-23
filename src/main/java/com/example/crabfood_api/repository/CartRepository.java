package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    @Query("SELECT c FROM CartItem c WHERE c.userId = ?1 AND c.foodId = ?2")
    CartItem findByUserIdAndFoodId(Long userId, Long foodId);
}
