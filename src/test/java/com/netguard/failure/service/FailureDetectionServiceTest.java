
package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailureDetectionServiceTest {

    private FailureDetectionService failureDetectionService;

    @BeforeEach
    void setUp() {
        failureDetectionService = new FailureDetectionService();
    }

    @Test
    void shouldDetectDeviceFailure() {
        MonitoringData data = new MonitoringData();
        data.setDevice("8.8.8.8");
        data.setStatus("DOWN");

        AlertEvent alert = failureDetectionService.analyze(data);

        assertNotNull(alert);
        assertEquals("FAILURE", alert.getEvent());
        assertEquals(AlertSeverity.CRITICAL, alert.getSeverity());
    }

    @Test
    void shouldNotRepeatDeviceFailureAlert() {
        MonitoringData data = new MonitoringData();
        data.setDevice("8.8.8.8");
        data.setStatus("DOWN");

        failureDetectionService.analyze(data);
        AlertEvent alert = failureDetectionService.analyze(data);

        assertNull(alert);
    }

    @Test
    void shouldDetectHighLatency() {
        MonitoringData data = new MonitoringData();
        data.setDevice("8.8.8.8");
        data.setStatus("UP");
        data.setLatency(150.0);

        AlertEvent alert = failureDetectionService.analyze(data);

        assertNotNull(alert);
        assertEquals("WARNING", alert.getEvent());
        assertEquals(AlertSeverity.WARNING, alert.getSeverity());
    }

    @Test
    void shouldDetectHighPacketLoss() {
        MonitoringData data = new MonitoringData();
        data.setDevice("8.8.8.8");
        data.setStatus("UP");
        data.setPacketLoss(15.0);

        AlertEvent alert = failureDetectionService.analyze(data);

        assertNotNull(alert);
        assertEquals("WARNING", alert.getEvent());
    }

    @Test
    void shouldDetectRecovery() {
        MonitoringData downData = new MonitoringData();
        downData.setDevice("8.8.8.8");
        downData.setStatus("DOWN");

        failureDetectionService.analyze(downData);

        MonitoringData upData = new MonitoringData();
        upData.setDevice("8.8.8.8");
        upData.setStatus("UP");

        AlertEvent alert = failureDetectionService.analyze(upData);

        assertNotNull(alert);
        assertEquals("RECOVERY", alert.getEvent());
        assertEquals(AlertSeverity.NORMAL, alert.getSeverity());
    }
}
