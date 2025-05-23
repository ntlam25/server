package com.example.crabfood_api.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuResponse {
    private Long vendorId;
    private String vendorName;
    private List<CategoryResponse> categories;
}
