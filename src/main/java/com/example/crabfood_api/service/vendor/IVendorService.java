package com.example.crabfood_api.service.vendor;


import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.request.VendorStatusUpdateRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.MenuResponse;
import com.example.crabfood_api.dto.response.PageResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.service.BaseCRUDService;

public interface IVendorService extends BaseCRUDService<VendorRequest, VendorResponse, Vendor, Long> {

    PageResponse<VendorResponse> getNearbyVendors(
        VendorSearchRequest request
    );

    PageResponse<VendorResponse> searchByNameOrAddress(VendorSearchRequest request);

    PageResponse<CategoryResponse> getCategoriesByVendorId(Long vendorId, int limit, int offset);

    PageResponse<FoodResponse> getFoodsByVendorId(Long vendorId, int limit, int offset);

    MenuResponse getMenu(Long vendorId, boolean isActive);

    VendorResponse findByUserId(Long userId);
    VendorResponse toggleVendorStatus(Long id, boolean status);
}
