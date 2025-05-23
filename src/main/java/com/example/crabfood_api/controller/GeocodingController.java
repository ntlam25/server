package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.service.maps.GoogleMapsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/geocode")
public class GeocodingController {

    @Value("${google.api.key}")
    private String apiKey;

    private final GoogleMapsService service;

    public GeocodingController(GoogleMapsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> geocode(@RequestParam String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + apiKey;
            System.out.println(urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(conn.getInputStream());

            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                JsonNode firstResult = results.get(0);
                JsonNode location = firstResult.path("geometry").path("location");
                double lat = location.path("lat").asDouble();
                double lng = location.path("lng").asDouble();
                // sử dụng lat, lng
                Map<String, Object> result = new HashMap<>();
                result.put("lat", location.path("lat").asDouble());
                result.put("lng", location.path("lng").asDouble());
                return ResponseEntity.ok(result);

            } else {
                // xử lý khi không có kết quả (ví dụ: địa chỉ không tìm thấy)
                throw new RuntimeException("No results found in Google Geocoding response");
            }


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<AddressResponse> extractAddress(GpsCoordinates coordinates) {
        return new ResponseEntity<>(service.getAddressFromCoordinates(coordinates), HttpStatus.OK);
    }
}
