package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailureDetectionServiceTest {

    @Test
    void shouldDetectDeviceFailure() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData data =
                new MonitoringData("Router-1", "DOWN", 0.0, 0.0);

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("FAILURE", result.getEvent());
        assertEquals(AlertSeverity.CRITICAL, result.getSeverity());
    }

    @Test
    void shouldPreventDuplicateFailureAlert() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData data =
                new MonitoringData("Router-1", "DOWN", 0.0, 0.0);

        AlertEvent first = service.analyze(data);
        AlertEvent second = service.analyze(data);

        assertNotNull(first);
        assertNull(second);
    }

    @Test
    void shouldDetectRecovery() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData down =
                new MonitoringData("Router-1", "DOWN", 0.0, 0.0);

        MonitoringData up =
                new MonitoringData("Router-1", "UP", 20.0, 0.0);

        service.analyze(down);

        AlertEvent recovery = service.analyze(up);

        assertNotNull(recovery);
        assertEquals("RECOVERY", recovery.getEvent());
        assertEquals(AlertSeverity.NORMAL, recovery.getSeverity());
    }

    @Test
    void shouldDetectHighLatency() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData data =
                new MonitoringData("Router-1", "UP", 150.0, 0.0);

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("WARNING", result.getEvent());
        assertEquals(AlertSeverity.WARNING, result.getSeverity());
    }

    @Test
    void shouldDetectHighPacketLoss() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData data =
                new MonitoringData("Router-1", "UP", 20.0, 15.0);

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("WARNING", result.getEvent());
        assertEquals(AlertSeverity.WARNING, result.getSeverity());
    }

    @Test
    void shouldDetectNormalNetworkCondition() {

        FailureDetectionService service = new FailureDetectionService();

        MonitoringData data =
                new MonitoringData("Router-1", "UP", 20.0, 2.0);

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("NORMAL", result.getEvent());
        assertEquals(AlertSeverity.NORMAL, result.getSeverity());
    }
}