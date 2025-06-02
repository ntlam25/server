package com.example.crabfood_api.service.category;

import com.example.crabfood_api.dto.request.CategoryRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Category;
import com.example.crabfood_api.service.BaseCRUDService;

import java.util.List;

public interface ICategoryService extends BaseCRUDService<CategoryRequest, CategoryResponse, Category, Long> {
    List<CategoryResponse> getAllGlobalCategories();
    List<FoodResponse> getFoodsByCategoryId(Long categoryId);
    List<CategoryResponse> findByVendorId(Long id);

    CategoryResponse toggleCategoryStatus(Long id);
}
