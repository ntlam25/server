package com.example.crabfood_api.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFoodResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private double foodPrice;
    private String foodImageUrl;
    private int quantity;
    private Long optionId;
    private List<OrderFoodChoiceResponse> choices;
}