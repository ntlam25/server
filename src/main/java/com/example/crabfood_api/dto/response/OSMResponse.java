package com.example.crabfood_api.dto.response;

import java.util.HashMap;
import java.util.Map;

import com.example.crabfood_api.dto.request.GpsCoordinates;

import lombok.Data;

@Data
public class OSMResponse {
    // Cấu trúc dữ liệu OSM chuẩn
    private Map<String, String> addressComponents = new HashMap<>();
    private String displayName;
    private GpsCoordinates coordinates;
    private String osmId;
    private String osmType;
}