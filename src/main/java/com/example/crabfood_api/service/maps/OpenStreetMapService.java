package com.example.crabfood_api.service.maps;


import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.OSMResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenStreetMapService {
    private static final Logger logger = LoggerFactory.getLogger(OpenStreetMapService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OSMResponse getAddressFromCoordinates(GpsCoordinates coords) {
        String url = String.format(
            "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=%s&lon=%s",
            coords.getLatitude(),
            coords.getLongitude()
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "YourApp/1.0 (contact@example.com)");
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseRawOSMResponse(response.getBody(), coords);
            }
        } catch (JsonProcessingException e) {
            logger.error("OSM API request failed", e);
        }
        return createDefaultOSMResponse(coords);
    }

    private OSMResponse parseRawOSMResponse(String json, GpsCoordinates coords) throws JsonProcessingException{
        JsonNode root = objectMapper.readTree(json);
        OSMResponse result = new OSMResponse();
        
        // Xử lý các thành phần địa chỉ
        JsonNode address = root.path("address");
        Map<String, String> components = new LinkedHashMap<>();
        
        address.fields().forEachRemaining(entry -> 
            components.put(entry.getKey(), entry.getValue().asText()));

        // Lấy thông tin OSM
        result.setOsmType(root.path("osm_type").asText());
        result.setOsmId(root.path("osm_id").asText());
        result.setDisplayName(root.path("display_name").asText());
        result.setCoordinates(coords);
        result.setAddressComponents(components);

        return result;
    }

    public AddressResponse convertToAddressResponse(OSMResponse osmAddress) {
        Map<String, String> addr = osmAddress.getAddressComponents();
        
        return AddressResponse.builder()
            .fullAddress(osmAddress.getDisplayName())
            .latitude(osmAddress.getCoordinates().getLatitude())
            .longitude(osmAddress.getCoordinates().getLongitude())
            .isDefault(false)
            .build();
    }

    private OSMResponse createDefaultOSMResponse(GpsCoordinates coords) {
        OSMResponse result = new OSMResponse();
        result.setCoordinates(coords);
        result.setDisplayName("Unknown Location");
        result.setOsmId("");
        result.setOsmType("");
        
        Map<String, String> defaultAddress = new HashMap<>();
        defaultAddress.put("error", "Address not found");
        result.setAddressComponents(defaultAddress);
        
        return result;
    }
}