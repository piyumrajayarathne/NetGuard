
package com.netguard.monitoring;

import com.netguard.monitoring.service.NetworkReachabilityService;
import com.netguard.failure.model.MonitoringData;

public class NetworkMonitoringDemo {

    public static void main(String[] args) {

        NetworkReachabilityService service =
                new NetworkReachabilityService();

        MonitoringData result =
        service.checkReachability("8.8.8.8", 1000, 3);

        System.out.println("===== NetGuard Monitoring =====");
        System.out.println("Device: " + result.getDevice());
        System.out.println("Status: " + result.getStatus());
        System.out.println("Latency: " + result.getLatency());
    }
}
