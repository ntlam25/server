package com.example.crabfood_api.service.order;

import com.example.crabfood_api.dto.request.CategoryRequest;
import com.example.crabfood_api.dto.request.OrderRequest;
import com.example.crabfood_api.dto.response.CategoryResponse;
import com.example.crabfood_api.dto.response.OrderResponse;
import com.example.crabfood_api.dto.response.VNPayResponse;
import com.example.crabfood_api.dto.websocket.OrderTrackingDTO;
import com.example.crabfood_api.model.entity.Category;
import com.example.crabfood_api.model.entity.Order;
import com.example.crabfood_api.service.BaseCRUDService;

import java.util.List;
import java.util.Map;

public interface IOrderService extends BaseCRUDService<OrderRequest, OrderResponse, Order, Long> {
    List<OrderResponse> getCustomerOrders(Long customerId);
    List<OrderResponse> getVendorOrders(Long vendorId);
    List<OrderResponse> getCustomerOrdersUpcoming(Long customerId);
    List<OrderResponse> getCustomerOrdersHistory(Long customerId);
    OrderResponse getOrder(Long orderId, Long customerId);
    OrderResponse updateOrderStatus(Long orderId, String status, Long userId);
    OrderResponse cancelOrder(Long orderId, String reason, Long customerId);
    VNPayResponse createPaymentUrl(Long orderId);
    VNPayResponse processPaymentCallback(Map<String, String> vnpayResponse);
    OrderTrackingDTO getOrderTrackingInfo(Long orderId);
}
