package com.example.crabfood_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSyncRequest {
    @JsonProperty("items")
    private List<CartItemDTO> cartItems;
}

