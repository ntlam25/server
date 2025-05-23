package com.example.crabfood_api.service.cart;

import com.example.crabfood_api.dto.request.CartItemDTO;

import java.util.List;

public interface ICartService {
    public List<CartItemDTO> getCartByUserId(Long userId);
    public void syncCart(Long userId, List<CartItemDTO> cartItems);
    public void clearCart(Long userId);
}
