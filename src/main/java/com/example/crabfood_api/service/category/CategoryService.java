package com.example.crabfood_api.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.crabfood_api.dto.request.CategoryRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Category;
import com.example.crabfood_api.repository.CategoryRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.util.Mapper;

@Service
public class CategoryService
        extends AbstractCrudService<CategoryRequest, CategoryResponse, CategoryRepository, Category, Long>
        implements ICategoryService{
    protected CategoryService(CategoryRepository repository) {
        super(repository, Category.class);
    }

    @Override
    protected Category createAndSave(CategoryRequest request) {
        return null;
    }

    @Override
    protected Category updateAndSave(Long id, CategoryRequest request) {
        return null;
    }

    @Override
    protected CategoryResponse toResponse(Category domainEntity) {
        return Mapper.toCategoryResponse(domainEntity);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getAllGlobalCategories() {
        return repository.findByIsGlobalIsTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> getFoodsByCategoryId(Long categoryId) {
        return repository.findFoodByCategoryId(categoryId).stream()
                .map(Mapper::toFoodResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> findByVendorId(Long id) {
        return repository.findByVendorIdOrIsGlobalIsTrue(id).stream()
                .map(Mapper::toCategoryResponse)
                .map(categoryResponse -> {
                    List<FoodResponse> filterFoods = categoryResponse.getFoods().stream()
                            .filter(foodResponse -> foodResponse.getVendorId() == id)
                            .toList();
                    categoryResponse.setFoods(filterFoods);
                    return categoryResponse;
                })
                .filter(categoryResponse -> !categoryResponse.getFoods().isEmpty())
                .toList();
    }
}
