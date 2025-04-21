package com.example.crabfood_api.service.vendor;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.crabfood_api.dto.request.VendorRequest;
import com.example.crabfood_api.dto.request.VendorSearchRequest;
import com.example.crabfood_api.dto.response.PageResponse;
import com.example.crabfood_api.dto.response.VendorResponse;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.repository.VendorRepository;
import com.example.crabfood_api.service.AbstractCrudService;
import com.example.crabfood_api.util.Mapper;

@Service
public class VendorService extends AbstractCrudService<VendorRequest, VendorResponse, VendorRepository, Vendor,Long> implements IVendorService {

    
    protected VendorService(VendorRepository repository) {
        super(repository, Vendor.class);
    }


    @Override
    public List<VendorResponse> searchByNameOrAddress(String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByNameOrAddress'");
    }

    @Override
    public List<VendorResponse> filterVendor(boolean isOpen, double minRating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'filterVendor'");
    }

    @Override
    protected Vendor createAndSave(VendorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAndSave'");
    }

    @Override
    protected Vendor updateAndSave(Long id, VendorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAndSave'");
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
            request.getLatitude(),
            request.getLongitude(),
            request.getRadius(),
            request.getCuisineType(),
            request.getMinRating(),
            PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.ASC, "location"))
        );

        var result = vendors.map(vendor -> {
            VendorResponse response = toResponse(vendor);
            
            double distance = userLocation.distance(vendor.getLocation()) / 1000;
            response.setDistance(Math.round(distance * 100.0) / 100.0);
            
            return response;
        });

        return new PageResponse<>(result);
    }
}