
package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import com.netguard.monitoring.model.NetworkDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkMonitoringService {

    private final PingReachabilityService pingReachabilityService;

    // Constructor injection
    public NetworkMonitoringService(
            PingReachabilityService pingReachabilityService) {

        this.pingReachabilityService = pingReachabilityService;
    }

    // Monitor multiple configured devices using ping
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

            results.add(result);
        }

        return results;
    }
}
