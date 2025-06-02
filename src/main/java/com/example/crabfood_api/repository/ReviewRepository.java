package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Page<Review> findByVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);

    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT AVG(r.foodRating) FROM Review r WHERE r.vendor.id = :vendorId AND r.foodRating IS NOT NULL")
    Double getAverageFoodRatingByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT AVG(r.deliveryRating) FROM Review r WHERE r.rider.id = :riderId AND r.deliveryRating IS NOT NULL")
    Double getAverageDeliveryRatingByRiderId(@Param("riderId") Long riderId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId")
    Long getTotalReviewsByVendorId(@Param("vendorId") Long vendorId);
}