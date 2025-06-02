package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.CategoryRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.service.category.ICategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final ICategoryService service;

    public CategoryController(ICategoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/global")
    public ResponseEntity<List<CategoryResponse>> getAllGlobalCategories() {
        return new ResponseEntity<>(service.getAllGlobalCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(service.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/foods")
    public ResponseEntity<List<FoodResponse>> getFoodsByCategoryId(@PathVariable Long id) {
        var foodResponseList = service.getFoodsByCategoryId(id);
        if (foodResponseList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foodResponseList, HttpStatus.OK);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<CategoryResponse>> findByVendorId(@PathVariable Long vendorId) {
        return new ResponseEntity<>(service.findByVendorId(vendorId), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CategoryResponse> updateCategoryStatus(@PathVariable Long id) {
        return new ResponseEntity<>(service.toggleCategoryStatus(id), HttpStatus.OK);
    }
}
