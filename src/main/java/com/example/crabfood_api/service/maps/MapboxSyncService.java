package com.example.crabfood_api.service.maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.MapboxGeocodingResult;
import com.example.crabfood_api.dto.response.MapboxResponse;

@Service
public class MapboxSyncService {
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String reverseGeocodeUrl;

    public MapboxSyncService(RestTemplate restTemplate,
                             @Value(value = "${mapbox.api.reverse-geocode-url}")
                             String reverseGeocodeUrl,
                             @Value("${mapbox.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.reverseGeocodeUrl = reverseGeocodeUrl;
        this.apiKey = apiKey;
    }

    public MapboxGeocodingResult reverseGeocode(GpsCoordinates coordinates) {
        double lng = coordinates.getLongitude(); // Note: Mapbox uses longitude first
        double lat = coordinates.getLatitude();
        String url = String.format("%s/%f,%f.json?access_token=%s&language=vi",
                reverseGeocodeUrl, lng, lat, apiKey);

        MapboxResponse response = restTemplate.getForObject(url, MapboxResponse.class);

        if (response != null && response.getFeatures() != null && !response.getFeatures().isEmpty()) {
            var feature = response.getFeatures().get(0);
            var context = feature.getContext();

            return MapboxGeocodingResult.builder()
                    .address(MapboxGeocodingResult.Address.builder()
                            .label(feature.getPlaceName())
                            .houseNumber(feature.getProperties().getHousenum())
                            .street(feature.getText())
                            .postalCode(findContextValue(context, "postcode"))
                            .district(findContextValue(context, "district"))
                            .city(findContextValue(context, "place"))
                            .ward(findContextValue(context, "neighborhood"))
                            .countryName(findContextValue(context, "country"))
                            .build())
                    .title(feature.getText())
                    .build();
        }
        return null;
    }

    private String findContextValue(java.util.List<MapboxResponse.Context> context, String key) {
        if (context == null) return null;
        return context.stream()
                .filter(c -> c.getId().startsWith(key + "."))
                .findFirst()
                .map(MapboxResponse.Context::getText)
                .orElse(null);
    }
}