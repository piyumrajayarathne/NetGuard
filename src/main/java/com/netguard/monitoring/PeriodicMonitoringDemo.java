
package com.netguard.monitoring;

import com.netguard.monitoring.service.NetworkMonitoringService;
import com.netguard.monitoring.service.PeriodicMonitoringService;
import com.netguard.monitoring.service.PingReachabilityService;

public class PeriodicMonitoringDemo {

    public static void main(String[] args) {

        PingReachabilityService pingService =
                new PingReachabilityService();

        NetworkMonitoringService monitoringService =
                new NetworkMonitoringService(pingService);

        PeriodicMonitoringService periodicService =
                new PeriodicMonitoringService(monitoringService);

        periodicService.startMonitoring();

        try {
            Thread.sleep(16000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            periodicService.stopMonitoring();
            System.out.println("\nMonitoring stopped.");
        }
    }
}
