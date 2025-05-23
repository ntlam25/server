package com.example.crabfood_api.repository;

import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.model.entity.Vendor;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Sử dụng PostGIS function ST_Distance (đơn vị mét)
    @Query(nativeQuery = true, countQuery = """
    SELECT COUNT(v.id) 
    FROM vendors v
    WHERE
        ST_Distance_Sphere(POINT(:#{#criteria.longitude}, :#{#criteria.latitude}), v.location) <= :#{#criteria.radius} * 1000
        AND v.is_approved = true
        AND (:#{#criteria.cuisineType} IS NULL OR v.cuisine_type LIKE CONCAT('%', :#{#criteria.cuisineType}, '%'))
        AND (:#{#criteria.minRating} IS NULL OR v.rating >= :#{#criteria.minRating})
        AND (
            :#{#criteria.isOpen} IS NULL 
            OR (
                :#{#criteria.isOpen} = true 
                AND CURRENT_TIME BETWEEN v.opening_time AND v.closing_time
                AND v.is_open = true
            )
            OR (
                :#{#criteria.isOpen} = false 
                AND (CURRENT_TIME NOT BETWEEN v.opening_time AND v.closing_time OR v.is_open = false)
            )
        )
    """, value = """
    SELECT v.* 
    FROM vendors v
    WHERE
        ST_Distance_Sphere(POINT(:#{#criteria.longitude}, :#{#criteria.latitude}), v.location) <= :#{#criteria.radius} * 1000
        AND v.is_approved = true
        AND (:#{#criteria.cuisineType} IS NULL OR v.cuisine_type LIKE CONCAT('%', :#{#criteria.cuisineType}, '%'))
        AND (:#{#criteria.minRating} IS NULL OR v.rating >= :#{#criteria.minRating})
        AND (
            :#{#criteria.isOpen} IS NULL 
            OR (
                :#{#criteria.isOpen} = true 
                AND CURRENT_TIME BETWEEN v.opening_time AND v.closing_time
                AND v.is_open = true
            )
            OR (
                :#{#criteria.isOpen} = false 
                AND (CURRENT_TIME NOT BETWEEN v.opening_time AND v.closing_time OR v.is_open = false)
            )
        )
    ORDER BY
        CASE 
            WHEN :#{#criteria.sortBy} = 'distance' THEN ST_Distance_Sphere(POINT(:#{#criteria.longitude}, :#{#criteria.latitude}), v.location)
            WHEN :#{#criteria.sortBy} = 'rating' THEN v.rating
            ELSE ST_Distance_Sphere(POINT(:#{#criteria.longitude}, :#{#criteria.latitude}), v.location)
        END
    """)
    Page<Vendor> findNearbyVendors(
            @Param("criteria") VendorSearchRequest criteria,
            Pageable pageable);

    // Tìm kiếm theo tên hoặc địa chỉ
    @Query("SELECT v FROM Vendor v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vendor> searchByNameOrAddress(@Param("keyword") String keyword, Pageable pageable);

    Optional<Vendor> findByUserId(Long id);
}
