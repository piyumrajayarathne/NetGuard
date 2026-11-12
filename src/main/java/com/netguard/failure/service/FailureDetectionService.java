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

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private final Map<String, AlertSeverity> previousStates = new HashMap<>();

    public AlertEvent analyze(MonitoringData data) {

        if (data == null) {
            return null;
        }

        String device = data.getDevice();

        if (STATUS_DOWN.equalsIgnoreCase(data.getStatus())) {

            AlertSeverity previousState = previousStates.get(device);

            if (previousState != AlertSeverity.CRITICAL) {

                previousStates.put(device, AlertSeverity.CRITICAL);

                return new AlertEvent(
                        device,
                        "FAILURE",
                        AlertSeverity.CRITICAL,
                        "Device unreachable"
                );
            }

            return null;
        }

        // Device has recovered
        if (STATUS_UP.equalsIgnoreCase(data.getStatus()) &&
                previousStates.get(device) == AlertSeverity.CRITICAL) {

            previousStates.put(device, AlertSeverity.NORMAL);

            return new AlertEvent(
                    device,
                    "RECOVERY",
                    AlertSeverity.NORMAL,
                    "Device recovered and is reachable again"
            );
        }

        if (data.getResponseTime() != null &&
                data.getResponseTime() >= RESPONSE_TIMEOUT_THRESHOLD) {

            AlertSeverity previousState = previousStates.get(device);

            if (previousState != AlertSeverity.CRITICAL) {

                previousStates.put(device, AlertSeverity.CRITICAL);

                return new AlertEvent(
                        device,
                        "FAILURE",
                        AlertSeverity.CRITICAL,
                        "Response timeout detected"
                );
            }

            return null;
        }

        if (data.getPacketLoss() != null &&
                data.getPacketLoss() >= PACKET_LOSS_WARNING_THRESHOLD) {

            AlertSeverity previousState = previousStates.get(device);

            if (previousState != AlertSeverity.WARNING) {

                previousStates.put(device, AlertSeverity.WARNING);

                return new AlertEvent(
                        device,
                        "WARNING",
                        AlertSeverity.WARNING,
                        "High packet loss detected"
                );
            }

            return null;
        }

        if (data.getLatency() != null &&
                data.getLatency() >= LATENCY_WARNING_THRESHOLD) {

            AlertSeverity previousState = previousStates.get(device);

            if (previousState != AlertSeverity.WARNING) {

                previousStates.put(device, AlertSeverity.WARNING);

                return new AlertEvent(
                        device,
                        "WARNING",
                        AlertSeverity.WARNING,
                        "High latency detected"
                );
            }

            return null;
        }

        previousStates.put(device, AlertSeverity.NORMAL);

        return new AlertEvent(
                device,
                "NORMAL",
                AlertSeverity.NORMAL,
                "Network operating normally"
        );
    }
}