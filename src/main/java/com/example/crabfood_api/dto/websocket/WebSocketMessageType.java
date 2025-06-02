package com.example.crabfood_api.dto.websocket;

public enum WebSocketMessageType {
    ORDER_CREATED,
    ORDER_UPDATED,
    ORDER_STATUS_CHANGED,
    ORDER_TRACKING_UPDATE,
    RIDER_LOCATION_UPDATE
}
