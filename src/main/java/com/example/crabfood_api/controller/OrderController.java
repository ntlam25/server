package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.OrderRequest;
import com.example.crabfood_api.dto.response.OrderResponse;
import com.example.crabfood_api.dto.response.VNPayResponse;
import com.example.crabfood_api.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }
    @PostMapping("/online")
    public ResponseEntity<VNPayResponse> createOrderWithPayment(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        VNPayResponse vnPayResponse = orderService.createPaymentUrl(response.getId());
        return new ResponseEntity<>(vnPayResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId, @RequestParam Long customerId) {
        return new ResponseEntity<>(orderService.getOrder(orderId, customerId), HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<OrderResponse>> getUpcomingOrders(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrdersUpcoming(customerId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getHistoryOrders(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrdersHistory(customerId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long customerId) {
        return new ResponseEntity<>(orderService.getCustomerOrders(customerId), HttpStatus.OK);
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam Long customerId) {
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId, status, customerId), HttpStatus.OK);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam String reason,
            @RequestParam Long customerId) {
        return new ResponseEntity<>(orderService.cancelOrder(orderId, reason, customerId), HttpStatus.OK);
    }
}