package com.example.crabfood_api.controller;


import com.example.crabfood_api.dto.request.RegisterVendorRequest;
import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorStatusUpdateRequest;
import com.example.crabfood_api.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.service.vendor.IVendorService;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final IVendorService service;

    public VendorController(IVendorService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> findById(@PathVariable Long id) {
        VendorResponse response = service.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<PageResponse<VendorResponse>> getVendorsByLocation(
            @RequestParam(required = true) Double latitude,
            @RequestParam(required = true) Double longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radius,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isOpen,
            @RequestParam(required = false, defaultValue = "distance") String sortBy) {

        VendorSearchRequest request = VendorSearchRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .limit(limit)
                .offset(offset)
                .cuisineType(cuisineType)
                .minRating(minRating)
                .isOpen(isOpen)
                .sortBy(sortBy)
                .build();

        return new ResponseEntity<>(service.getNearbyVendors(request), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<VendorResponse>> searchVendorsByNameOrAddress(
            @RequestParam(required = true) String keyword,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        VendorSearchRequest request = VendorSearchRequest.builder()
                .keyword(keyword)
                .limit(limit)
                .offset(offset)
                .latitude(latitude)
                .longitude(longitude)
                .radius(5.0)
                .build();

        return new ResponseEntity<>(service.searchByNameOrAddress(request), HttpStatus.OK);
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<PageResponse<CategoryResponse>> getCategoriesByVendorId(@PathVariable Long id,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return new ResponseEntity<>(service.getCategoriesByVendorId(id, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/{id}/foods")
    public ResponseEntity<PageResponse<FoodResponse>> getFoodsByVendorId(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return new ResponseEntity<>(service.getFoodsByVendorId(id, limit, offset), HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<VendorResponse> findByUserId(@PathVariable Long userId){
        return new ResponseEntity<>(service.findByUserId(userId), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VendorResponse> updateVendorStatus(@PathVariable Long id) {
        return new ResponseEntity<>(service.toggleVendorStatus(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<VendorResponse> approveVendor(@PathVariable Long id) {
        return new ResponseEntity<>(service.approveVendor(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<VendorResponse> rejectVendor(@PathVariable Long id) {
        return new ResponseEntity<>(service.rejectVendor(id), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<VendorResponse> update(@PathVariable Long id, @RequestBody VendorRequest request) {
        return new ResponseEntity<>(service.update(id, request), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<VendorRegistrationResponse> registerVendor(
            @Valid @RequestBody RegisterVendorRequest request) {
        VendorRegistrationResponse response = service.registerVendor(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VendorResponse>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

}
