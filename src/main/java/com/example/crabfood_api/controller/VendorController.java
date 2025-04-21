package com.example.crabfood_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.response.PageResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.service.vendor.IVendorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final IVendorService service;

    @PostMapping("/vendors-nearby")
    public ResponseEntity<PageResponse<VendorResponse>> getVendors(
            @Valid @RequestBody VendorSearchRequest request) {

        PageResponse<VendorResponse> response = service.getNearbyVendors(request);
        
        return ResponseEntity.ok(response);
    }

//    // 2. Tìm kiếm theo tên/địa chỉ
//    @GetMapping("/search")
//    public ResponseEntity<List<VendorResponse>> searchRestaurants(
//        @RequestParam String keyword) {
//
//        return ResponseEntity.ok(service.searchByNameOrAddress(keyword));
//    }
//
//    // 3. Lọc nhà hàng (mở cửa, đánh giá...)
//    @GetMapping("/filter")
//    public ResponseEntity<List<VendorResponse>> filterRestaurants(
//        @RequestParam(required = false) Boolean isOpen,
//        @RequestParam(required = false) Double minRating) {
//
//        return ResponseEntity.ok(service.filterVendor(isOpen, minRating));
//    }
}
