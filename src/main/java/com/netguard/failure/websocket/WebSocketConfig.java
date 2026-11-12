package com.netguard.failure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NetworkWebSocketHandler networkWebSocketHandler;

    public WebSocketConfig(NetworkWebSocketHandler networkWebSocketHandler) {
        this.networkWebSocketHandler = networkWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry) {

        registry.addHandler(networkWebSocketHandler, "/ws/network")
                .setAllowedOriginPatterns("*");
    }
}
