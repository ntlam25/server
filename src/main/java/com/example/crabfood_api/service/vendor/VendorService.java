package com.example.crabfood_api.service.vendor;


import java.util.List;

import com.example.crabfood_api.dto.request.VendorStatusUpdateRequest;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.MenuResponse;
import com.example.crabfood_api.dto.response.PageResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.Category;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.repository.CategoryRepository;
import com.example.crabfood_api.repository.FoodRepository;
import com.example.crabfood_api.repository.VendorRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.util.DistanceUtils;
import com.example.crabfood_api.util.Mapper;

@Service
public class VendorService extends AbstractCrudService<VendorRequest, VendorResponse, VendorRepository, Vendor, Long> implements IVendorService {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;

    protected VendorService(VendorRepository repository, CategoryRepository categoryRepository, 
        FoodRepository foodRepository) {
        super(repository, Vendor.class);
        this.categoryRepository = categoryRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public PageResponse<VendorResponse> searchByNameOrAddress(VendorSearchRequest request) {
        String keyword = request.getKeyword();
        int limit = request.getLimit();
        int offset = request.getOffset();
        Page<Vendor> vendors = repository.searchByNameOrAddress(keyword, PageRequest.of(offset, limit));
        if (vendors.isEmpty()) {
            return new PageResponse<>();
        }
        var result = vendors.map(vendor -> {
            VendorResponse response = toResponse(vendor);
            // Tính khoảng cách từ người dùng đến vendor
            if (request.getLongitude() != null && request.getLatitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                Point userLocation = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
                double distance = DistanceUtils.calculateDistance(userLocation, vendor.getLocation());
                response.setDistance(Math.round(distance * 100.0) / 100.0);
            } else {
                response.setDistance(0.0);
            }
            return response;
        });
        return new PageResponse<>(result);
    }

    @Override
    protected Vendor createAndSave(VendorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAndSave'");
    }

    @Override
    protected Vendor updateAndSave(Long id, VendorRequest request) {
        Vendor vendor = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vendor not found"));
        vendor.setName(request.getName());
        vendor.setAddress(request.getAddress());
        vendor.setClosingTime(request.getClosingTime());
        vendor.setOpeningTime(request.getOpeningTime());
        vendor.setCoverImageUrl(request.getCoverImageUrl());
        vendor.setLogoUrl(request.getLogoUrl());
        vendor.setDeliveryFee(request.getDeliveryFee());
        vendor.setDescription(request.getDescription());
        vendor.setServiceRadiusKm(request.getServiceRadiusKm());
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        vendor.setLocation(location);
        vendor.setCuisineType(request.getCuisineType());
        return repository.save(vendor);
    }

    @Override
    protected VendorResponse toResponse(Vendor domainEntity) {
        return Mapper.toVendorResponse(domainEntity);
    }

    @Override
    public PageResponse<VendorResponse> getNearbyVendors(VendorSearchRequest request) {
        // 1. Tạo Point từ tọa độ người dùng
        GeometryFactory geometryFactory = new GeometryFactory();
        Point userLocation = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        // 2. Gọi repository với phân trang
        Page<Vendor> vendors = repository.findNearbyVendors(
                request,
                PageRequest.of(request.getOffset(), request.getLimit())
        );

        var result = vendors.map(vendor -> {
            VendorResponse response = toResponse(vendor);

            double distance = DistanceUtils.calculateDistance(userLocation, vendor.getLocation());
            response.setDistance(Math.round(distance * 100.0) / 100.0);
            return response;
        });

        return new PageResponse<>(result);
    }

    @Override
    public PageResponse<CategoryResponse> getCategoriesByVendorId(Long vendorId, int limit, int offset) {
        var result = categoryRepository.findByVendorIdOrGlobal(vendorId, PageRequest.of(offset, limit))
                .map(Mapper::toCategoryResponse);
        return new PageResponse<>(result);
    }

    @Override
    public PageResponse<FoodResponse> getFoodsByVendorId(Long vendorId, int limit, int offset) {
        var result = foodRepository.findByVendorId(vendorId, PageRequest.of(offset, limit))
                .map(Mapper::toFoodResponse);
        return new PageResponse<>(result);
    }

    @Override
    public MenuResponse getMenu(Long vendorId, boolean isActive) {
        Vendor vendor = repository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        List<Category> categories = categoryRepository.findByVendorIdAndIsActive(vendorId, isActive);        

        return MenuResponse.builder()
            .vendorId(vendor.getId())
            .vendorName(vendor.getName())
            .categories(categories.stream()
                .map(Mapper::toCategoryResponse)
                .toList())
            .build();
    }

    @Override
    public VendorResponse findByUserId(Long userId) {
        return Mapper.toVendorResponse(repository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("Vendor not found")
        ));
    }

    @Override
    public VendorResponse toggleVendorStatus(Long id, boolean status) {
        Vendor vendor = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Vendor not found"));
        vendor.setActive(status);
        return Mapper.toVendorResponse(repository.save(vendor));
    }
}
