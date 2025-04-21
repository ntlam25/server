package com.example.crabfood_api.service.vendor;

import java.util.List;

import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.response.PageResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.service.BaseCRUDService;

public interface IVendorService extends BaseCRUDService<VendorRequest, VendorResponse, Vendor, Long> {

    PageResponse<VendorResponse> getNearbyVendors(
        VendorSearchRequest request
    );

    public List<VendorResponse> searchByNameOrAddress(String keyword);

    public List<VendorResponse> filterVendor(boolean isOpen, double minRating);
}
