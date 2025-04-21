package com.example.crabfood_api.service.maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.crabfood_api.dto.request.GpsCoordinates;
import com.example.crabfood_api.dto.response.AddressResponse;
import com.example.crabfood_api.dto.response.GoogleMapsResponse;
import com.example.crabfood_api.exception.AddressRetrievalException;

@Service
public class GoogleMapsService {
    private final RestTemplate restTemplate;

    private final String apiKey;
    private final String geocodeUrl;

    public GoogleMapsService(RestTemplateBuilder restTemplateBuilder,
            @Value(value = "${google.maps.api.key}") String apiKey,
            @Value(value = "${google.maps.geocode.url}") String geocodeUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = apiKey;
        this.geocodeUrl = geocodeUrl;
    }

    public AddressResponse getAddressFromCoordinates(GpsCoordinates coordinates) {
        String url = String.format(
                "%s?latlng=%s,%s&key=%s",
                geocodeUrl, coordinates.getLatitude(), coordinates.getLongitude(), apiKey);

        GoogleMapsResponse response = restTemplate.getForObject(url, GoogleMapsResponse.class);

        if (response == null || !"OK".equals(response.getStatus())) {
            throw new AddressRetrievalException("Failed to get address from GPS");
        }

        GoogleMapsResponse.Result firstResult = response.getResults().get(0);
        return extractAddressDetails(firstResult);
    }

    private AddressResponse extractAddressDetails(GoogleMapsResponse.Result result) {
        AddressResponse.AddressResponseBuilder builder = AddressResponse.builder()
                .fullAddress(result.getFormattedAddress());

        for (GoogleMapsResponse.AddressComponent component : result.getAddressComponents()) {
            if (component.getTypes().contains("locality")) {
                builder.city(component.getLongName());
            } else if (component.getTypes().contains("administrative_area_level_2")) {
                builder.district(component.getLongName());
            } else if (component.getTypes().contains("administrative_area_level_3")
                    || component.getTypes().contains("sublocality")) {
                builder.ward(component.getLongName());
            }
        }

        return builder.build();
    }
}
