package com.netguard.failure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.MonitoringData;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NetworkWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("WebSocket client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws IOException {

        System.out.println("Received message: " + message.getPayload());

        session.sendMessage(
                new TextMessage("Server received: " + message.getPayload())
        );
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) {

        sessions.remove(session);
        System.out.println("WebSocket client disconnected: " + session.getId());
    }

    // Broadcast monitoring data to all connected clients
    public void broadcastMonitoringData(MonitoringData data) {
        sendToAllClients("MONITORING_DATA", data);
    }

    // Broadcast alert events to all connected clients
    public void broadcastAlert(AlertEvent alert) {
        sendToAllClients("ALERT_EVENT", alert);
    }

    // Common method for sending JSON messages
    private void sendToAllClients(String type, Object data) {

        try {
            String jsonMessage = objectMapper.writeValueAsString(
                    new WebSocketMessage(type, data)
            );

            for (WebSocketSession session : sessions) {

                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(jsonMessage));
                    } catch (IOException e) {
                        System.err.println(
                                "Failed to send message to client: "
                                        + e.getMessage()
                        );
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "Failed to convert message to JSON: " + e.getMessage()
            );
        }
    }

    // Message structure sent to frontend
    private static class WebSocketMessage {

        private final String type;
        private final Object data;

        public WebSocketMessage(String type, Object data) {
            this.type = type;
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public Object getData() {
            return data;
        }
    }
}
