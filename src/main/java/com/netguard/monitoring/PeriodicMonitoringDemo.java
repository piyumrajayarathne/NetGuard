
package com.netguard.monitoring;

import com.netguard.monitoring.service.NetworkMonitoringService;
import com.netguard.monitoring.service.NetworkReachabilityService;
import com.netguard.monitoring.service.PeriodicMonitoringService;

public class PeriodicMonitoringDemo {

    public static void main(String[] args) throws InterruptedException {

        NetworkReachabilityService reachabilityService =
                new NetworkReachabilityService();

        NetworkMonitoringService monitoringService =
                new NetworkMonitoringService(reachabilityService);

        PeriodicMonitoringService periodicService =
                new PeriodicMonitoringService(monitoringService);

        periodicService.startMonitoring();

        // Allow monitoring to run for 20 seconds
        Thread.sleep(20000);

        periodicService.stopMonitoring();

        System.out.println("Monitoring stopped.");
    }
}
