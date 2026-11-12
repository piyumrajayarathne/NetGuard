package com.netguard.failure.controller;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.service.NetworkCommunicationService;
import com.netguard.failure.websocket.NetworkWebSocketHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    private final NetworkCommunicationService communicationService;
    private final NetworkWebSocketHandler webSocketHandler;

    public CommunicationController(
            NetworkCommunicationService communicationService,
            NetworkWebSocketHandler webSocketHandler) {

        this.communicationService = communicationService;
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping("/monitoring")
    public ResponseEntity<String> sendMonitoringData(
            @RequestBody MonitoringData data) {

        communicationService.processMonitoringData(data);

        return ResponseEntity.ok(
                "Monitoring data processed and broadcast successfully"
        );
    }

    @PostMapping("/alert")
    public ResponseEntity<String> sendAlert(
            @RequestBody AlertEvent alert) {

        webSocketHandler.broadcastAlert(alert);

        return ResponseEntity.ok(
                "Alert broadcast successfully"
        );
    }
}