package com.netguard.failure.controller;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.websocket.NetworkWebSocketHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    private final NetworkWebSocketHandler webSocketHandler;

    public CommunicationController(
            NetworkWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    // Receive and broadcast monitoring data
    @PostMapping("/monitoring")
    public ResponseEntity<String> sendMonitoringData(
            @RequestBody MonitoringData data) {

        webSocketHandler.broadcastMonitoringData(data);

        return ResponseEntity.ok(
                "Monitoring data broadcast successfully"
        );
    }

    // Receive and broadcast alert events
    @PostMapping("/alert")
    public ResponseEntity<String> sendAlert(
            @RequestBody AlertEvent alert) {

        webSocketHandler.broadcastAlert(alert);

        return ResponseEntity.ok(
                "Alert broadcast successfully"
        );
    }
}
