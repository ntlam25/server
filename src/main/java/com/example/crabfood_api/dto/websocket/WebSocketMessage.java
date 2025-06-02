package com.example.crabfood_api.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {
    private WebSocketMessageType type;
    private T payload;
}
