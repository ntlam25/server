package com.example.crabfood_api.service.vendor;


import com.example.crabfood_api.dto.request.RegisterVendorRequest;
import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.request.VendorStatusUpdateRequest;
import com.example.crabfood_api.dto.response.*;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.service.BaseCRUDService;

import java.util.List;

public interface IVendorService extends BaseCRUDService<VendorRequest, VendorResponse, Vendor, Long> {

    PageResponse<VendorResponse> getNearbyVendors(
        VendorSearchRequest request
    );

    PageResponse<VendorResponse> searchByNameOrAddress(VendorSearchRequest request);

    PageResponse<CategoryResponse> getCategoriesByVendorId(Long vendorId, int limit, int offset);

    PageResponse<FoodResponse> getFoodsByVendorId(Long vendorId, int limit, int offset);

    VendorResponse findByUserId(Long userId);
    VendorResponse toggleVendorStatus(Long id);
    VendorResponse approveVendor(Long id);
    VendorResponse rejectVendor(Long id);
    VendorResponse setFavoriteVendor(Long id);
    List<VendorResponse> findFavoriteVendors();

    VendorRegistrationResponse registerVendor(RegisterVendorRequest request);
    List<TopVendorResponse> getTopVendors(int limit);
    // Alternative method using simple approach
    List<TopVendorResponse> getTopVendorsSimple(int limit);

}
