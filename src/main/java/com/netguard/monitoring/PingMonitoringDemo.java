package com.netguard.monitoring;

import com.netguard.failure.model.MonitoringData;
import com.netguard.monitoring.service.PingReachabilityService;

public class PingMonitoringDemo {

    public static void main(String[] args) {

        PingReachabilityService pingService =
                new PingReachabilityService();

        String[] devices = {
                "8.8.8.8",
                "1.1.1.1",
                "google.com"
        };

        System.out.println("===== NetGuard Ping Monitoring =====");

        for (String device : devices) {

            System.out.println("\nMonitoring: " + device);

            try {
                MonitoringData result =
                        pingService.checkReachability(
                                device,
                                2000,
                                3
                        );

                System.out.println("Status: "
                        + result.getStatus());

                System.out.println("Average Latency: "
                        + result.getLatency() + " ms");

                System.out.println("Packet Loss: "
                        + result.getPacketLoss() + "%");

            } catch (Exception e) {
                System.out.println("Monitoring error: "
                        + e.getMessage());
            }
        }
    }
}