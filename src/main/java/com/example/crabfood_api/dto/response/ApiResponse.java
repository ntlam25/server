package com.example.crabfood_api.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;
}
