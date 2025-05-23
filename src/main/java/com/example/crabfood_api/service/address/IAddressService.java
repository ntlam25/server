package com.example.crabfood_api.service.address;

import com.example.crabfood_api.dto.request.AddressRequest;
import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.HereGeocodingResult;
import com.example.crabfood_api.model.entity.Address;
import com.example.crabfood_api.service.BaseCRUDService;

import java.util.List;

public interface IAddressService extends BaseCRUDService<AddressRequest, AddressResponse, Address, Long> {
    HereGeocodingResult getAddressFromGps(GpsCoordinates coordinates);
    List<AddressResponse> getUserAddresses();
}
