
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
public class SocketReachabilityService {

    public MonitoringData checkReachability(
            String device,
            int port,
            int timeoutMillis,
            int attempts) {

        // Validate input
        if (device == null || device.isBlank()) {
            throw new IllegalArgumentException(
                    "Device address cannot be empty");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Port must be between 1 and 65535");
        }

        if (timeoutMillis <= 0 || attempts <= 0) {
            throw new IllegalArgumentException(
                    "Timeout and attempts must be greater than zero");
        }

        int successfulConnections = 0;
        double totalLatency = 0;

        for (int i = 0; i < attempts; i++) {

            try (Socket socket = new Socket()) {

                long startTime = System.nanoTime();

                socket.connect(
                        new InetSocketAddress(device, port),
                        timeoutMillis
                );

                long endTime = System.nanoTime();

                successfulConnections++;

                totalLatency +=
                        (endTime - startTime) / 1_000_000.0;

            } catch (IOException e) {
                // This connection attempt failed.
            }
        }

        int failedConnections =
                attempts - successfulConnections;

        double connectionFailureRate =
                (failedConnections * 100.0) / attempts;

        Double averageLatency = null;

        if (successfulConnections > 0) {
            averageLatency =
                    totalLatency / successfulConnections;
        }

        String status = successfulConnections > 0
                ? "REACHABLE"
                : "UNREACHABLE";

        return new MonitoringData(
                device,
                status,
                averageLatency,
                connectionFailureRate
        );
    }
}
