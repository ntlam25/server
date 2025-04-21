package com.example.crabfood_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.crabfood_api.model.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Sử dụng PostGIS function ST_Distance (đơn vị mét)
    @Query(nativeQuery = true, value = """
            SELECT v.*
            FROM vendors v
            WHERE
                ST_Distance_Sphere(POINT(:lng, :lat), v.location) <= :radius * 1000
                AND v.is_approved = true
                AND (:cuisineType IS NULL OR v.cuisine_type LIKE CONCAT('%', :cuisineType, '%'))
                AND (:minRating IS NULL OR v.rating >= :minRating)
            """)
    Page<Vendor> findNearbyVendors(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radius,
            @Param("cuisineType") String cuisineType,
            @Param("minRating") Double minRating,
            Pageable pageable);

    @Query(value = "SELECT v.* FROM vendors v " +
            "WHERE (:cuisineType IS NULL OR v.cuisine_type = :cuisineType) " +
            "AND v.rating >= :minRating " +
            "AND v.is_open = :isOpen " +
            "AND v.is_approved = true", nativeQuery = true)
    Page<Vendor> findByFilters(
            @Param("cuisineType") String cuisineType,
            @Param("minRating") Double minRating,
            @Param("isOpen") Boolean isOpen,
            Pageable pageable);
}
