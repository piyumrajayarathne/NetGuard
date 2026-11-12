package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import com.netguard.monitoring.model.NetworkDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkMonitoringService {

    private final SocketReachabilityService socketReachabilityService;

    // Constructor injection
    public NetworkMonitoringService(
            SocketReachabilityService socketReachabilityService) {

        this.socketReachabilityService = socketReachabilityService;
    }

    // Monitor multiple configured devices
    public List<MonitoringData> monitorDevices(
            List<NetworkDevice> devices,
            int timeoutMillis,
            int attempts) {

        List<MonitoringData> results = new ArrayList<>();

        for (NetworkDevice device : devices) {

            MonitoringData result =
                    socketReachabilityService.checkReachability(
                            device.getAddress(),
                            device.getPort(),
                            timeoutMillis,
                            attempts
                    );

            results.add(result);
        }

        return results;
    }
}