package com.example.crabfood_api.controller;


import com.example.crabfood_api.dto.request.AddressRequest;
import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.HereGeocodingResult;
import com.example.crabfood_api.service.address.AddressService;
import com.example.crabfood_api.service.address.IAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final IAddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressRequest request) {
        return new  ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/address-by-user")
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        return new ResponseEntity<>(service.getUserAddresses(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        return new ResponseEntity<>(service.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/gps-lookup")
    public ResponseEntity<HereGeocodingResult> getAddressFromGps(
        @RequestParam double latitude,
        @RequestParam double longitude) 
    {
        GpsCoordinates coords = new GpsCoordinates(latitude, longitude);
        return new ResponseEntity<>(service.getAddressFromGps(coords), HttpStatus.OK);
    }
}