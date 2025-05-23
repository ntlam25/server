package com.example.crabfood_api.dto.response;

public class GeocodingResponse {
    public Location location;

    public static class Location {
        public double lat;
        public double lng;
    }
}
