package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.service.food.IFoodService;
import com.example.crabfood_api.service.vendor.IVendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    private final IVendorService vendorService;
    private final IFoodService foodService;

    public FavoriteController(IVendorService vendorService, IFoodService foodService) {
        this.vendorService = vendorService;
        this.foodService = foodService;
    }


    @PatchMapping("/vendor/{id}")
    public ResponseEntity<VendorResponse> setFavoriteVendor(@PathVariable Long id){
        return new ResponseEntity<>(vendorService.setFavoriteVendor(id), HttpStatus.OK);
    }

    @PatchMapping("/food/{id}")
    public ResponseEntity<FoodResponse> setFavoriteFood(@PathVariable Long id){
        return new ResponseEntity<>(foodService.setFavoriteFood(id), HttpStatus.OK);
    }

    @GetMapping("/food/favorite")
    public ResponseEntity<List<FoodResponse>> findFavoriteFoods(@PathVariable Long id){
        return new ResponseEntity<>(foodService.findFavoriteFoods(), HttpStatus.OK);
    }

    @GetMapping("/vendor/favorite")
    public ResponseEntity<List<VendorResponse>> findFavoriteVendors(@PathVariable Long id){
        return new ResponseEntity<>(vendorService.findFavoriteVendors(), HttpStatus.OK);
    }


}
