package com.example.crabfood_api.dto.response;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FoodOptionResponse {
    private Long optionId;
    private String name;
    private boolean isRequired;
    private int minSelection;
    private int maxSelection;
    
    private List<OptionChoiceResponse> choices;
}
