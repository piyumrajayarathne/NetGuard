package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import com.netguard.monitoring.model.NetworkDevice;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NetworkMonitoringServiceTest {

    @Test
    void shouldMonitorMultipleDevices() {

        // Create a mock socket monitoring service
        SocketReachabilityService socketService =
                mock(SocketReachabilityService.class);

        // Create the service we want to test
        NetworkMonitoringService monitoringService =
                new NetworkMonitoringService(socketService);

        // Configure two network devices
        List<NetworkDevice> devices = Arrays.asList(

                new NetworkDevice(
                        "Google DNS",
                        "8.8.8.8",
                        53
                ),

                new NetworkDevice(
                        "Cloudflare DNS",
                        "1.1.1.1",
                        53
                )
        );

        // Define simulated monitoring results
        MonitoringData googleResult =
                new MonitoringData(
                        "8.8.8.8",
                        "REACHABLE",
                        25.5,
                        0.0
                );

        MonitoringData cloudflareResult =
                new MonitoringData(
                        "1.1.1.1",
                        "REACHABLE",
                        30.2,
                        0.0
                );

        // Tell Mockito what results to return
        when(socketService.checkReachability(
                "8.8.8.8", 53, 3000, 5))
                .thenReturn(googleResult);

        when(socketService.checkReachability(
                "1.1.1.1", 53, 3000, 5))
                .thenReturn(cloudflareResult);

        // Run the monitoring service
        List<MonitoringData> results =
                monitoringService.monitorDevices(
                        devices,
                        3000,
                        5
                );

        // Check the results
        assertEquals(2, results.size());

        assertEquals("8.8.8.8", results.get(0).getDevice());
        assertEquals("1.1.1.1", results.get(1).getDevice());

        assertEquals("REACHABLE", results.get(0).getStatus());
        assertEquals("REACHABLE", results.get(1).getStatus());

        // Verify that both devices were checked correctly
        verify(socketService).checkReachability(
                "8.8.8.8", 53, 3000, 5);

        verify(socketService).checkReachability(
                "1.1.1.1", 53, 3000, 5);
    }
}