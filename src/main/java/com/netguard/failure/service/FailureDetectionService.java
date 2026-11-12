package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

@Service
public class FailureDetectionService {

    private static final double LATENCY_WARNING_THRESHOLD = 100.0;
    private static final double PACKET_LOSS_WARNING_THRESHOLD = 10.0;

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private final Map<String, AlertSeverity> previousStates = new HashMap<>();

    public AlertEvent analyze(MonitoringData data) {

        if (data == null) {
            return null;
        }

        String device = data.getDevice();

        if ("DOWN".equalsIgnoreCase(data.getStatus())) {

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

        if ("UP".equalsIgnoreCase(data.getStatus()) &&
                previousStates.get(device) == AlertSeverity.CRITICAL) {

            previousStates.put(device, AlertSeverity.NORMAL);

            return new AlertEvent(
                    device,
                    "RECOVERY",
                    AlertSeverity.NORMAL,
                    "Device recovered and is reachable again"
            );
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
        @Test
    void shouldReturnToNormalAfterWarning() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData warning =
                new MonitoringData("Router-1", "UP", 150.0, 0.0);

        MonitoringData normal =
                new MonitoringData("Router-1", "UP", 20.0, 2.0);

        AlertEvent first = service.analyze(warning);
        AlertEvent second = service.analyze(normal);

        assertNotNull(first);
        assertEquals("WARNING", first.getEvent());
        assertEquals(AlertSeverity.WARNING, first.getSeverity());

        assertNotNull(second);
        assertEquals("NORMAL", second.getEvent());
        assertEquals(AlertSeverity.NORMAL, second.getSeverity());
    }

    @Test
    void shouldDetectCriticalFailureAfterWarning() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData warning =
                new MonitoringData("Router-1", "UP", 150.0, 0.0);

        MonitoringData down =
                new MonitoringData("Router-1", "DOWN", 0.0, 0.0);

        AlertEvent first = service.analyze(warning);
        AlertEvent second = service.analyze(down);

        assertNotNull(first);
        assertEquals("WARNING", first.getEvent());

        assertNotNull(second);
        assertEquals("FAILURE", second.getEvent());
        assertEquals(AlertSeverity.CRITICAL, second.getSeverity());
    }
}