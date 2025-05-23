package com.example.crabfood_api.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCategoryResponse {
    private Long id;
    private String name;
}
