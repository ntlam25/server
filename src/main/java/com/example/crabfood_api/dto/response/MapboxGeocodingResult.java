package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapboxGeocodingResult {
    private String title;
    private Address address;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String label;
        private String houseNumber;
        private String street;
        private String postalCode;
        private String district;
        private String city;
        private String ward;
        private String countryName;
    }
}
