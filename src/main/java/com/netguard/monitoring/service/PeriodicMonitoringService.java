
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.service.NetworkCommunicationService;
import com.netguard.monitoring.model.NetworkDevice;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PeriodicMonitoringService {

    private final NetworkMonitoringService monitoringService;
    private final NetworkCommunicationService communicationService;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public PeriodicMonitoringService(
            NetworkMonitoringService monitoringService,
            NetworkCommunicationService communicationService) {

        this.monitoringService = monitoringService;
        this.communicationService = communicationService;
    }

    public void startMonitoring() {

        List<NetworkDevice> devices = Arrays.asList(

                new NetworkDevice("Google DNS", "8.8.8.8", 53),

                new NetworkDevice("Cloudflare DNS", "1.1.1.1", 53),

                new NetworkDevice("Google HTTPS", "google.com", 443)
        );

        scheduler.scheduleAtFixedRate(() -> {

            System.out.println(
                    "\n===== NetGuard Periodic Ping Monitoring ====="
            );

            List<MonitoringData> results =
                    monitoringService.monitorDevices(
                            devices,
                            3000,
                            5
                    );

            for (MonitoringData result : results) {

                // Send monitoring data to Member 3's communication service
                communicationService.processMonitoringData(result);

                System.out.println("\nDevice: " + result.getDevice());
                System.out.println("Status: " + result.getStatus());

                System.out.println(
                        "Average Latency: " +
                        (result.getLatency() != null
                                ? result.getLatency() + " ms"
                                : "N/A")
                );

                System.out.println(
                        "Packet Loss: " +
                        result.getPacketLoss() + "%"
                );
            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {

        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
