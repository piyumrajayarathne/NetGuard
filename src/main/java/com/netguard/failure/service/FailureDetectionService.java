package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

@Service
public class FailureDetectionService {

    private static final double LATENCY_WARNING_THRESHOLD = 100.0;
    private static final double PACKET_LOSS_WARNING_THRESHOLD = 10.0;

    public AlertEvent analyze(MonitoringData data) {

        if (data == null) {
            return null;
        }

        if ("DOWN".equalsIgnoreCase(data.getStatus())) {

            return new AlertEvent(
                    data.getDevice(),
                    "FAILURE",
                    AlertSeverity.CRITICAL,
                    "Device unreachable"
            );
        }

        if (data.getPacketLoss() != null &&
                data.getPacketLoss() >= PACKET_LOSS_WARNING_THRESHOLD) {

            return new AlertEvent(
                    data.getDevice(),
                    "WARNING",
                    AlertSeverity.WARNING,
                    "High packet loss detected"
            );
        }

        if (data.getLatency() != null &&
                data.getLatency() >= LATENCY_WARNING_THRESHOLD) {

            return new AlertEvent(
                    data.getDevice(),
                    "WARNING",
                    AlertSeverity.WARNING,
                    "High latency detected"
            );
        }

        return new AlertEvent(
                data.getDevice(),
                "NORMAL",
                AlertSeverity.NORMAL,
                "Network operating normally"
        );
    }
}