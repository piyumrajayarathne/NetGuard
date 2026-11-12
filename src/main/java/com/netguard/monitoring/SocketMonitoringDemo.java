
package com.netguard.monitoring;

import com.netguard.failure.model.MonitoringData;
import com.netguard.monitoring.model.NetworkDevice;
import com.netguard.monitoring.service.SocketReachabilityService;

import java.util.Arrays;
import java.util.List;

public class SocketMonitoringDemo {

    public static void main(String[] args) {

        SocketReachabilityService service =
                new SocketReachabilityService();

        // Configure network devices
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
                ),

                new NetworkDevice(
                        "Google HTTPS",
                        "google.com",
                        443
                )
        );

        System.out.println("===== NetGuard Monitoring =====");

        // Monitor each configured device
        for (NetworkDevice device : devices) {

            MonitoringData result =
                    service.checkReachability(
                            device.getAddress(),
                            device.getPort(),
                            3000,
                            5
                    );

            System.out.println("\nDevice: "
                    + device.getDeviceName());

            System.out.println("Address: "
                    + device.getAddress());

            System.out.println("Port: "
                    + device.getPort());

            System.out.println("Status: "
                    + result.getStatus());

            System.out.println("Latency: "
                    + result.getLatency() + " ms");

            System.out.println("Connection Failure Rate: "
                    + result.getPacketLoss() + "%");
        }
    }
}
