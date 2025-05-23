package com.example.crabfood_api.repository;


import com.example.crabfood_api.dto.response.FoodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crabfood_api.model.entity.Food;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findByVendorId(Long vendorId, Pageable pageable);

    // Lấy danh sách món ăn nỔi bật của vendor
    @Query(value = "SELECT * FROM foods f WHERE f.vendor_id = :vendorId and f.is_featured = true " +
            "ORDER BY f.rating DESC LIMIT :limit",
            nativeQuery = true)
    List<Food> findTopFeaturedByVendor(@Param("vendorId") Long vendorId, @Param("limit") int limit);

    @Query("SELECT f FROM Food f JOIN f.categories c WHERE c.id = :categoryId")
    List<Food> findByCategoryId(Long categoryId);

    List<Food> findAllByIsDeletedIsFalse();

    @Query(value = """
        SELECT f.*, SUM(odf.quantity) AS order_count
        FROM foods f
        LEFT JOIN order_foods odf ON f.id = odf.food_id
        WHERE f.vendor_id = :vendorId
        GROUP BY f.id
        ORDER BY order_count DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Food> findPopularFoodByVendorId(@Param("vendorId") Long vendorId, @Param("limit") int limit);

    @Query(value = "SELECT * from foods f WHERE f.vendor_id = :vendorId GROUP BY f.id" +
            " ORDER BY f.created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Food> findNewFoodsByVendorId(@Param("vendorId") Long vendorId, @Param("limit") int limit);

    @Query("""
    SELECT f FROM Food f
                JOIN f.categories c
                WHERE f.vendor.id = :vendorId AND c.id = :categoryId
                AND f.isAvailable = true
""")
    List<Food> findByVendorIdAndCategoryId(@Param("vendorId") Long vendorId, @Param("categoryId") Long categoryId);

    List<Food> findByVendorId(Long vendorId);
}
