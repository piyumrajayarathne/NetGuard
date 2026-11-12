
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkMonitoringService {

    private final NetworkReachabilityService reachabilityService;

    public NetworkMonitoringService(
            NetworkReachabilityService reachabilityService) {
        this.reachabilityService = reachabilityService;
    }

    public List<MonitoringData> monitorDevices(
            List<String> devices,
            int timeoutMillis) {

        List<MonitoringData> results = new ArrayList<>();

        for (String device : devices) {

           MonitoringData result =
        reachabilityService.checkReachability(
                device, timeoutMillis, 3);

            results.add(result);
        }

        return results;
    }
}
