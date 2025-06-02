package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.FoodReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodReviewRepository extends JpaRepository<FoodReview, Long> {

    List<FoodReview> findByFoodIdOrderByCreatedAtDesc(Long foodId);

    @Query("SELECT AVG(fr.rating) FROM FoodReview fr WHERE fr.food.id = :foodId AND fr.rating IS NOT NULL")
    Double getAverageRatingByFoodId(@Param("foodId") Long foodId);

    @Query("SELECT COUNT(fr) FROM FoodReview fr WHERE fr.food.id = :foodId")
    Long getTotalReviewsByFoodId(@Param("foodId") Long foodId);
}
