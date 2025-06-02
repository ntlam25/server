package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFoodChoiceResponse {
    private Long id;
    private String optionName;
    private String choiceName;
    private double priceAdjustment;
    private Long optionId;
}
