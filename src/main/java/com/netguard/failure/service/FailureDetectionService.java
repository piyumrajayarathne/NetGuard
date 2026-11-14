
package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FailureDetectionService {

    private static final double LATENCY_WARNING_THRESHOLD = 100.0;
    private static final double PACKET_LOSS_WARNING_THRESHOLD = 10.0;
    private static final double RESPONSE_TIMEOUT_THRESHOLD = 5000.0;

    // Remember the exact previous condition for each device
    private final Map<String, String> previousStates = new HashMap<>();

    private boolean isDeviceUp(String status) {
        return "UP".equalsIgnoreCase(status)
                || "REACHABLE".equalsIgnoreCase(status);
    }

    private boolean isDeviceDown(String status) {
        return "DOWN".equalsIgnoreCase(status)
                || "UNREACHABLE".equalsIgnoreCase(status);
    }

    public AlertEvent analyze(MonitoringData data) {

        if (data == null || data.getDevice() == null) {
            return null;
        }

        String device = data.getDevice();
        String currentState = "NORMAL";
        String reason = "Network operating normally";
        AlertSeverity severity = AlertSeverity.NORMAL;
        String event = "NORMAL";

        if (isDeviceDown(data.getStatus())) {

            currentState = "DEVICE_DOWN";
            reason = "Device unreachable";
            severity = AlertSeverity.CRITICAL;
            event = "FAILURE";

        } else if (data.getResponseTime() != null &&
                data.getResponseTime() >= RESPONSE_TIMEOUT_THRESHOLD) {

            currentState = "RESPONSE_TIMEOUT";
            reason = "Response timeout detected";
            severity = AlertSeverity.CRITICAL;
            event = "FAILURE";

        } else if (data.getPacketLoss() != null &&
                data.getPacketLoss() >= PACKET_LOSS_WARNING_THRESHOLD) {

            currentState = "HIGH_PACKET_LOSS";
            reason = "High packet loss detected";
            severity = AlertSeverity.WARNING;
            event = "WARNING";

        } else if (data.getLatency() != null &&
                data.getLatency() >= LATENCY_WARNING_THRESHOLD) {

            currentState = "HIGH_LATENCY";
            reason = "High latency detected";
            severity = AlertSeverity.WARNING;
            event = "WARNING";
        }

        String previousState = previousStates.get(device);

        // Generate an alert only when the condition changes
        if (!currentState.equals(previousState)) {

            previousStates.put(device, currentState);

            if ("NORMAL".equals(currentState)) {

                // Send recovery only when recovering from a problem
                if (previousState != null &&
                        !"NORMAL".equals(previousState)) {

                    return new AlertEvent(
                            device,
                            "RECOVERY",
                            AlertSeverity.NORMAL,
                            "Device returned to normal operation"
                    );
                }

                return null;
            }

            return new AlertEvent(
                    device,
                    event,
                    severity,
                    reason
            );
        }

        // Same condition as the previous monitoring cycle
        return null;
    }
}
