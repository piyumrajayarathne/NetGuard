
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMonitoringServiceTest {

    private final NetworkReachabilityService reachabilityService =
            new NetworkReachabilityService();

    private final NetworkMonitoringService monitoringService =
            new NetworkMonitoringService(reachabilityService);

    @Test
    void shouldMonitorMultipleDevices() {

        List<String> devices = Arrays.asList(
                "8.8.8.8",
                "1.1.1.1"
        );

        List<MonitoringData> results =
                monitoringService.monitorDevices(devices, 3000);

        assertEquals(2, results.size());

        assertEquals("8.8.8.8", results.get(0).getDevice());
        assertEquals("1.1.1.1", results.get(1).getDevice());

        assertNotNull(results.get(0).getStatus());
        assertNotNull(results.get(1).getStatus());
    }
}
