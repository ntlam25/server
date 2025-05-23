package com.example.crabfood_api.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapboxResponse {
    private List<Feature> features;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feature {
        private String id;
        private String type;
        private String text;
        private String placeName;
        private Properties properties;
        private List<Context> context;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        private String housenum;
        private String accuracy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        private String id;
        private String text;
        private String wikidata;
        private String shortCode;
    }
}

