package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.FoodRequest;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.service.food.IFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final IFoodService service;

    public FoodController(IFoodService service) {
        this.service = service;
    }

    @GetMapping("/category/{categoryId}")
    public List<FoodResponse> findByCategoryId(@PathVariable Long categoryId) {
        return service.findByCategoryId(categoryId);
    }

    @GetMapping("/feature/{vendorId}")
    public ResponseEntity<List<FoodResponse>> findTopFoodsByVendorId(@PathVariable Long vendorId) {
        return new ResponseEntity<>(service.findTopFoodsByVendorId(vendorId, 8), HttpStatus.OK);
    }


    @GetMapping("/popular/{vendorId}")
    public ResponseEntity<List<FoodResponse>> findTopPopularFoods(@PathVariable Long vendorId) {
        return new ResponseEntity<>(service.findPopularFoodsByVendorId(vendorId, 8), HttpStatus.OK);
    }
    @GetMapping("/new/{vendorId}")
    public ResponseEntity<List<FoodResponse>> findTopNewFoodsByVendorId(@PathVariable Long vendorId) {
        return new ResponseEntity<>(service.findNewFoodsByVendorId(vendorId, 8), HttpStatus.OK);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<FoodResponse>> findByVendorId(@PathVariable Long vendorId) {
        return new ResponseEntity<>(service.findByVendorId(vendorId), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<FoodResponse>> findAll(){
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> findById(@PathVariable Long id){
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @GetMapping("/by")
    public ResponseEntity<List<FoodResponse>> findByVendorIdAndCategoryId(@RequestParam Long vendorId, @RequestParam Long categoryId){
        return new ResponseEntity<>(service.findByVendorIdAndCategoryId(vendorId, categoryId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FoodResponse> create(@RequestBody FoodRequest request){
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> update(@PathVariable Long id, @RequestBody FoodRequest request){
        FoodResponse updatedFood = service.update(id, request);
        if (updatedFood == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedFood, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FoodResponse> updateAvailableStatus(@PathVariable Long id, @RequestParam boolean available) {
        return new ResponseEntity<>(service.updateStatusAvailable(id, available), HttpStatus.OK);
    }
}
