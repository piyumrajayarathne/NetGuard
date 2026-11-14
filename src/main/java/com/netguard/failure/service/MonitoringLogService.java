package com.netguard.failure.service;

import com.netguard.failure.entity.MonitoringLogEntity;
import com.netguard.failure.entity.NetworkDeviceEntity;
import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.repository.MonitoringLogRepository;
import com.netguard.failure.repository.NetworkDeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitoringLogService {

    private final MonitoringLogRepository repository;
    private final NetworkDeviceRepository networkDeviceRepository;

    public MonitoringLogService(
            MonitoringLogRepository repository,
            NetworkDeviceRepository networkDeviceRepository) {

        this.repository = repository;
        this.networkDeviceRepository = networkDeviceRepository;
    }

    public MonitoringLogEntity save(MonitoringData data) {

        NetworkDeviceEntity device =
                networkDeviceRepository
                        .findByAddress(data.getDevice())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Device not found: " + data.getDevice()
                                ));

        MonitoringLogEntity log = new MonitoringLogEntity(
                device,
                data.getStatus(),
                data.getLatency(),
                data.getPacketLoss(),
                data.getResponseTime()
        );

        return repository.save(log);
    }
}