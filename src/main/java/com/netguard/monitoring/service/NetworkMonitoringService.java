package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.service.MonitoringLogService;
import com.netguard.monitoring.model.NetworkDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkMonitoringService {

    private final PingReachabilityService pingReachabilityService;
    private final MonitoringLogService monitoringLogService;

    public NetworkMonitoringService(
            PingReachabilityService pingReachabilityService,
            MonitoringLogService monitoringLogService) {

        this.pingReachabilityService = pingReachabilityService;
        this.monitoringLogService = monitoringLogService;
    }

    public List<MonitoringData> monitorDevices(
            List<NetworkDevice> devices,
            int timeoutMillis,
            int attempts) {

        List<MonitoringData> results = new ArrayList<>();

        for (NetworkDevice device : devices) {

            MonitoringData result =
                    pingReachabilityService.checkReachability(
                            device.getAddress(),
                            timeoutMillis,
                            attempts
                    );

            monitoringLogService.save(result);

            results.add(result);
        }

        return results;
    }
}