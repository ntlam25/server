package com.example.crabfood_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionChoiceRequest {
    private Long id;
    private String name;
    private double priceAdjustment;  // Giá điều chỉnh nếu chọn tùy chọn này
    private boolean isDefault;       // Có phải là lựa chọn mặc định hay không
}