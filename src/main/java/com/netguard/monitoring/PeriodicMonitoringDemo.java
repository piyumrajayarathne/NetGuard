package com.netguard.monitoring;

import com.netguard.monitoring.service.NetworkMonitoringService;
import com.netguard.monitoring.service.PeriodicMonitoringService;
import com.netguard.monitoring.service.SocketReachabilityService;

public class PeriodicMonitoringDemo {

    public static void main(String[] args) {

        // Create the required services
        SocketReachabilityService socketService =
                new SocketReachabilityService();

        NetworkMonitoringService monitoringService =
                new NetworkMonitoringService(socketService);

        PeriodicMonitoringService periodicService =
                new PeriodicMonitoringService(monitoringService);

        // Start periodic monitoring
        periodicService.startMonitoring();

        // Allow monitoring to run for 16 seconds
        try {
            Thread.sleep(16000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } finally {
            // Stop the monitoring scheduler
            periodicService.stopMonitoring();
            System.out.println("\nMonitoring stopped.");
        }
    }
}