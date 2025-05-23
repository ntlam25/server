package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.LocationUpdateRequest;
import com.example.crabfood_api.dto.response.OrderTrackingInfo;
import com.example.crabfood_api.service.order.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderTrackingService trackingService;

    /**
     * REST endpoint to get tracking information for an order
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderTrackingInfo> getOrderTrackingInfo(
            @PathVariable Long orderId,
            @RequestParam Long customerId) {
        return ResponseEntity.ok(trackingService.getOrderTrackingInfo(orderId, customerId));
    }

    /**
     * WebSocket endpoint for location updates from riders
     */
    @MessageMapping("/location/update")
    public void updateLocation(@Payload LocationUpdateRequest locationUpdate) {
        trackingService.updateRiderLocation(locationUpdate);
    }

    /**
     * Alternative REST endpoint for location updates
     * (for clients that can't use WebSocket)
     */
    @PostMapping("/location/update")
    public ResponseEntity<Void> updateLocationRest(@RequestBody LocationUpdateRequest locationUpdate) {
        trackingService.updateRiderLocation(locationUpdate);
        return ResponseEntity.ok().build();
    }
}