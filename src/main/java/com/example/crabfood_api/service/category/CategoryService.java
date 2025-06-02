package com.example.crabfood_api.service.category;

import java.util.List;

import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.repository.VendorRepository;
import com.example.crabfood_api.util.StringHelper;
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
    private final VendorRepository vendorRepository;

    protected CategoryService(CategoryRepository repository,
                              VendorRepository vendorRepository) {
        super(repository, Category.class);
        this.vendorRepository = vendorRepository;
    }

    @Override
    protected Category createAndSave(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setGlobal(request.getIsGlobal());
        category.setActive(request.getIsActive());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setImageUrl(request.getImageUrl());
        category.setSlug(StringHelper.toSlug(request.getName()));

        if (request.getIsGlobal()){
            category.setVendor(null);
        } else {
            Vendor vendor = vendorRepository.findById(request.getVendorId()).orElseThrow(() ->
                    new ResourceNotFoundException("Vendor not found"));
            category.setVendor(vendor);
        }

        return repository.save(category);
    }

    @Override
    protected Category updateAndSave(Long id, CategoryRequest request) {
        Category category = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setGlobal(request.getIsGlobal());
        category.setActive(request.getIsActive());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setImageUrl(request.getImageUrl());
        category.setSlug(StringHelper.toSlug(request.getName()));

        return repository.save(category);
    }

    @Override
    protected CategoryResponse toResponse(Category domainEntity) {
        return Mapper.toCategoryResponse(domainEntity);
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

    @Override
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found"));
        category.setActive(!category.isActive());
        return Mapper.toCategoryResponse(repository.save(category));
    }
}
