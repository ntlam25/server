package com.example.crabfood_api.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOptionRequest {
    private Long id;
    private String name;
    private boolean isRequired;
    private int minSelection;
    private int maxSelection;
    private List<OptionChoiceRequest> choices; // Danh sách lựa chọn trong tùy chọn này
}