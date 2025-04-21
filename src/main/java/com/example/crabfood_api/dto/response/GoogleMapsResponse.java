package com.example.crabfood_api.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GoogleMapsResponse {
    private List<Result> results;
    private String status;

    @Data
    public static class Result {
        @JsonProperty("formatted_address")
        private String formattedAddress;
        private List<AddressComponent> addressComponents;
    }

    @Data
    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;
        @JsonProperty("short_name")
        private String shortName;
        private List<String> types;
    }
}