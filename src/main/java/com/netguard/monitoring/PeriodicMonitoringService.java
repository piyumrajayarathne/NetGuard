
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PeriodicMonitoringService {

    private final NetworkMonitoringService monitoringService;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public PeriodicMonitoringService(
            NetworkMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    public void startMonitoring() {

        List<String> devices = Arrays.asList(
                "8.8.8.8",
                "1.1.1.1"
        );

        scheduler.scheduleAtFixedRate(() -> {

            System.out.println("\n===== Periodic Network Monitoring =====");

            List<MonitoringData> results =
                    monitoringService.monitorDevices(devices, 3000);

            for (MonitoringData result : results) {
                System.out.println(result);
            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
    }
}
