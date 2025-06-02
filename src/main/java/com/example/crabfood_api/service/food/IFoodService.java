package com.example.crabfood_api.service.food;

import com.example.crabfood_api.dto.request.FoodRequest;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.model.entity.Food;
import com.example.crabfood_api.service.BaseCRUDService;

import java.util.List;

public interface IFoodService  extends BaseCRUDService<FoodRequest, FoodResponse, Food, Long> {
    List<FoodResponse> findByCategoryId(Long categoryId);
    List<FoodResponse> findByVendorId(Long vendorId);

    List<FoodResponse> findTopFoodsByVendorId(Long vendorId, int limit);

    List<FoodResponse> findPopularFoodsByVendorId(Long vendorId, int limit);
    List<FoodResponse> findNewFoodsByVendorId(Long vendorId, int limit);

    List<FoodResponse> findByVendorIdAndCategoryId(Long vendorId, Long categoryId);

    FoodResponse updateStatusAvailable(Long id, boolean available);
    FoodResponse setFavoriteFood(Long id);
    List<FoodResponse> findFavoriteFoods();
}
