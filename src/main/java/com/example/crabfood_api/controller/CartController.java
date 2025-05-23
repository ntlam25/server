package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.CartItemDTO;
import com.example.crabfood_api.dto.request.CartSyncRequest;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.service.cart.ICartService;
import com.example.crabfood_api.util.UserUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCart(@PathVariable Long userId) {
        List<CartItemDTO> cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/sync")
    public ResponseEntity<Void> syncCart(
            @RequestBody CartSyncRequest request, @PathVariable Long userId) {
        cartService.syncCart(userId, request.getCartItems());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}