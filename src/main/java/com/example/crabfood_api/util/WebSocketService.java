package com.example.crabfood_api.util;

import com.example.crabfood_api.dto.response.OrderResponse;
import com.example.crabfood_api.dto.websocket.WebSocketMessage;
import com.example.crabfood_api.dto.websocket.WebSocketMessageType;
import com.example.crabfood_api.model.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private static final String ORDER_TOPIC = "/topic/orders";

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendOrderCreated(OrderResponse order) {
        try {
            WebSocketMessage<OrderResponse> message = new WebSocketMessage<>(WebSocketMessageType.ORDER_CREATED, order);
            messagingTemplate.convertAndSend(ORDER_TOPIC, message);
            logger.info("Sent ORDER_CREATED message for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error sending ORDER_CREATED message", e);
        }
    }

    public void sendOrderUpdated(OrderResponse order) {
        try {
            WebSocketMessage<OrderResponse> message = new WebSocketMessage<>(WebSocketMessageType.ORDER_UPDATED, order);
            messagingTemplate.convertAndSend(ORDER_TOPIC, message);
            logger.info("Sent ORDER_UPDATED message for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error sending ORDER_UPDATED message", e);
        }
    }

    public void sendOrderStatusChanged(OrderResponse order) {
        try {
            WebSocketMessage<OrderResponse> message = new WebSocketMessage<>(WebSocketMessageType.ORDER_STATUS_CHANGED, order);
            messagingTemplate.convertAndSend(ORDER_TOPIC, message);
            logger.info("Sent ORDER_STATUS_CHANGED message for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error sending ORDER_STATUS_CHANGED message", e);
        }
    }
}