package com.example.crabfood_api.repository;


import java.util.List;

import com.example.crabfood_api.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.crabfood_api.model.entity.Category;
import com.example.crabfood_api.model.entity.Food;
import org.springframework.stereotype.Repository;

@Repository
public interface  CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy danh mục của vendor + danh mục global
    @Query("SELECT c FROM Category c WHERE c.vendor.id = :vendorId OR c.isGlobal = true")
    Page<Category> findByVendorIdOrGlobal(@Param("vendorId") Long vendorId, Pageable pageable);

    // Lấy danh sách danh mục global
    List<Category> findByIsGlobalIsTrue();

    @EntityGraph(attributePaths = {"foods", "foods.options", "foods.options.choices"})
    List<Category> findByVendorIdAndIsActive(Long vendorId, boolean isActive);

    // Lấy danh sách món ăn của danh mục
    @Query("SELECT f FROM Food f JOIN f.categories c WHERE c.id = :categoryId")
    @EntityGraph(attributePaths = {"foods", "foods.options", "foods.options.choices"})
    List<Food> findFoodByCategoryId(Long categoryId);

    List<Category> findByVendorIdOrIsGlobalIsTrue(Long id);
}
