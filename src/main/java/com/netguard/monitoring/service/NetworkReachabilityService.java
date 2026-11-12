
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class NetworkReachabilityService {

    public MonitoringData checkReachability(
            String device,
            int timeoutMillis,
            int attempts) {

        // Validate input
        if (device == null || device.isBlank()) {
            throw new IllegalArgumentException(
                    "Device address cannot be empty");
        }

        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException(
                    "Timeout must be greater than zero");
        }

        if (attempts <= 0) {
            throw new IllegalArgumentException(
                    "Number of attempts must be greater than zero");
        }

        int successfulProbes = 0;
        double totalLatency = 0;

        try {
            InetAddress address = InetAddress.getByName(device);

            for (int i = 0; i < attempts; i++) {

                long startTime = System.nanoTime();

                try {
                    boolean reachable =
                            address.isReachable(timeoutMillis);

                    long endTime = System.nanoTime();

                    if (reachable) {
                        successfulProbes++;

                        totalLatency +=
                                (endTime - startTime) / 1_000_000.0;
                    }

                } catch (IOException e) {
                    // This probe failed. Continue with the next probe.
                }
            }

        } catch (IOException e) {
            // Hostname resolution failed.
        }

        int failedProbes = attempts - successfulProbes;

        double packetLoss =
                (failedProbes * 100.0) / attempts;

        Double averageLatency = null;

        if (successfulProbes > 0) {
            averageLatency = totalLatency / successfulProbes;
        }

        String status = successfulProbes > 0
                ? "REACHABLE"
                : "UNREACHABLE";

        return new MonitoringData(
                device,
                status,
                averageLatency,
                packetLoss
        );
    }
}
