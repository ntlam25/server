package com.example.crabfood_api.dto.response;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OptionChoiceResponse {
    private Long id;
    private String name;
    private double priceAdjustment;
    private boolean isDefault;
}
