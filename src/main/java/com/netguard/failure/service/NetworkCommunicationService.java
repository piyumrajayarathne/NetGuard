
package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.websocket.NetworkWebSocketHandler;

import org.springframework.stereotype.Service;

@Service
public class NetworkCommunicationService {

    private final FailureDetectionService failureDetectionService;
    private final NetworkWebSocketHandler webSocketHandler;

    public NetworkCommunicationService(
            FailureDetectionService failureDetectionService,
            NetworkWebSocketHandler webSocketHandler) {

        this.failureDetectionService = failureDetectionService;
        this.webSocketHandler = webSocketHandler;
    }

    // Process monitoring data received from Member 1
    public void processMonitoringData(MonitoringData data) {

        if (data == null) {
            return;
        }

        // Convert Member 1 status into Member 2 expected status
        if ("REACHABLE".equalsIgnoreCase(data.getStatus())) {
            data.setStatus("UP");
        } else if ("UNREACHABLE".equalsIgnoreCase(data.getStatus())) {
            data.setStatus("DOWN");
        }

        // Broadcast monitoring data to connected clients
        webSocketHandler.broadcastMonitoringData(data);

        // Analyze monitoring data using Member 2's service
        AlertEvent alert = failureDetectionService.analyze(data);

        // Broadcast alert only when an event is generated
        if (alert != null && !"NORMAL".equalsIgnoreCase(alert.getEvent())) {
            webSocketHandler.broadcastAlert(alert);
        }
    }
}
