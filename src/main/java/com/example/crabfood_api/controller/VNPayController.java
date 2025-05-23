package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.response.VNPayResponse;
import com.example.crabfood_api.service.order.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final IOrderService orderService;

    public VNPayController(IOrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<VNPayResponse> createPaymentURL(@RequestParam Long orderId) {
        VNPayResponse response = orderService.createPaymentUrl(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-callback")
    public RedirectView vnPayCallback(@RequestParam Map<String, String> params) {
        VNPayResponse response = orderService.processPaymentCallback(params);
        return new RedirectView("crabfood://payment-result?orderId=" + response.getOrderId()+ "&status=" + response.getStatus());
    }
}