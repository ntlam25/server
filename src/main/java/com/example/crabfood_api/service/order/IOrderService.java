package com.example.crabfood_api.service.order;

import com.example.crabfood_api.dto.request.OrderRequest;
import com.example.crabfood_api.dto.response.OrderResponse;
import com.example.crabfood_api.dto.response.VNPayResponse;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getCustomerOrders(Long customerId);
    List<OrderResponse> getCustomerOrdersUpcoming(Long customerId);
    List<OrderResponse> getCustomerOrdersHistory(Long customerId);
    OrderResponse getOrder(Long orderId, Long customerId);
    OrderResponse updateOrderStatus(Long orderId, String status, Long customerId);
    OrderResponse cancelOrder(Long orderId, String reason, Long customerId);
    VNPayResponse createPaymentUrl(Long orderId);
    VNPayResponse processPaymentCallback(Map<String, String> vnpayResponse);
}
