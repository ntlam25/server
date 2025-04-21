package com.example.crabfood_api.service.address;

import com.example.crabfood_api.dto.request.AddressRequest;
import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.OSMResponse;
import com.example.crabfood_api.model.entity.Address;
import com.example.crabfood_api.service.BaseCRUDService;

public interface IAddressService extends BaseCRUDService<AddressRequest, AddressResponse, Address, Long> {
    public OSMResponse getAddressFromGps(GpsCoordinates coordinates);
}
