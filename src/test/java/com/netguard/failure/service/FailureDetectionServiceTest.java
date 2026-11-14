package com.netguard.failure.service;

import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.AlertSeverity;
import com.netguard.failure.model.MonitoringData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FailureDetectionServiceTest {

    private FailureDetectionService service;
    private AlertEventService alertEventService;

    @BeforeEach
    void setUp() {
        alertEventService = mock(AlertEventService.class);
        service = new FailureDetectionService(alertEventService);
    }

    @Test
    void shouldDetectDeviceFailure() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "DOWN",
                        null,
                        null
                );

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("192.168.1.10", result.getDevice());
        assertEquals("FAILURE", result.getEvent());
        assertEquals(AlertSeverity.CRITICAL, result.getSeverity());
        assertEquals("Device unreachable", result.getReason());

        verify(alertEventService).save(result);
    }

    @Test
    void shouldPreventDuplicateFailureAlert() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "DOWN",
                        null,
                        null
                );

        AlertEvent first = service.analyze(data);
        AlertEvent second = service.analyze(data);

        assertNotNull(first);
        assertNull(second);

        verify(alertEventService, times(1))
                .save(first);
    }

    @Test
    void shouldDetectRecovery() {

        MonitoringData down =
                new MonitoringData(
                        "192.168.1.10",
                        "DOWN",
                        null,
                        null
                );

        MonitoringData up =
                new MonitoringData(
                        "192.168.1.10",
                        "UP",
                        20.0,
                        0.0
                );

        service.analyze(down);

        AlertEvent recovery = service.analyze(up);

        assertNotNull(recovery);
        assertEquals("RECOVERY", recovery.getEvent());
        assertEquals(AlertSeverity.NORMAL, recovery.getSeverity());
        assertEquals(
                "Device recovered and is reachable again",
                recovery.getReason()
        );

        verify(alertEventService, times(2))
                .save(any(AlertEvent.class));
    }

    @Test
    void shouldDetectHighLatency() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "UP",
                        150.0,
                        0.0
                );

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("WARNING", result.getEvent());
        assertEquals(AlertSeverity.WARNING, result.getSeverity());
        assertEquals("High latency detected", result.getReason());

        verify(alertEventService).save(result);
    }

    @Test
    void shouldDetectHighPacketLoss() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "UP",
                        20.0,
                        25.0
                );

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("WARNING", result.getEvent());
        assertEquals(AlertSeverity.WARNING, result.getSeverity());
        assertEquals(
                "High packet loss detected",
                result.getReason()
        );

        verify(alertEventService).save(result);
    }

    @Test
    void shouldDetectNormalNetworkCondition() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "UP",
                        20.0,
                        0.0
                );

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("NORMAL", result.getEvent());
        assertEquals(AlertSeverity.NORMAL, result.getSeverity());
        assertEquals(
                "Network operating normally",
                result.getReason()
        );

        verify(alertEventService, never())
                .save(any(AlertEvent.class));
    }

    @Test
    void shouldDetectResponseTimeout() {

        MonitoringData data =
                new MonitoringData(
                        "192.168.1.10",
                        "UP",
                        50.0,
                        0.0,
                        6000.0
                );

        AlertEvent result = service.analyze(data);

        assertNotNull(result);
        assertEquals("FAILURE", result.getEvent());
        assertEquals(AlertSeverity.CRITICAL, result.getSeverity());
        assertEquals(
                "Response timeout detected",
                result.getReason()
        );

        verify(alertEventService).save(result);
    }
}