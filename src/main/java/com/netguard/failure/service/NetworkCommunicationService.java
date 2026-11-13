package com.netguard.failure.service;

import com.netguard.failure.entity.AlertEntity;
import com.netguard.failure.entity.MonitoringLogEntity;
import com.netguard.failure.entity.NetworkDeviceEntity;
import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.model.MonitoringData;
import com.netguard.failure.repository.AlertRepository;
import com.netguard.failure.repository.MonitoringLogRepository;
import com.netguard.failure.repository.NetworkDeviceRepository;
import com.netguard.failure.websocket.NetworkWebSocketHandler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NetworkCommunicationService {

    private final FailureDetectionService failureDetectionService;
    private final NetworkWebSocketHandler webSocketHandler;
    private final NetworkDeviceRepository networkDeviceRepository;
    private final MonitoringLogRepository monitoringLogRepository;
    private final AlertRepository alertRepository;

    public NetworkCommunicationService(
            FailureDetectionService failureDetectionService,
            NetworkWebSocketHandler webSocketHandler,
            NetworkDeviceRepository networkDeviceRepository,
            MonitoringLogRepository monitoringLogRepository,
            AlertRepository alertRepository) {

        this.failureDetectionService = failureDetectionService;
        this.webSocketHandler = webSocketHandler;
        this.networkDeviceRepository = networkDeviceRepository;
        this.monitoringLogRepository = monitoringLogRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void processMonitoringData(MonitoringData data) {

        if (data == null) {
            return;
        }

        // Convert monitoring status
        if ("REACHABLE".equalsIgnoreCase(data.getStatus())) {
            data.setStatus("UP");
        } else if ("UNREACHABLE".equalsIgnoreCase(data.getStatus())) {
            data.setStatus("DOWN");
        }

        // Find registered device using its IP address
        NetworkDeviceEntity device =
                networkDeviceRepository.findByAddress(data.getDevice())
                        .orElseThrow(() -> new IllegalStateException(
                                "Device not found: " + data.getDevice()));

        // Save monitoring history
        MonitoringLogEntity log = new MonitoringLogEntity(
                device,
                data.getStatus(),
                data.getLatency(),
                data.getPacketLoss(),
                data.getResponseTime()
        );

        monitoringLogRepository.save(log);

        // Analyze monitoring data
        AlertEvent alert = failureDetectionService.analyze(data);

        // Save and broadcast only actual alerts
        if (alert != null &&
                !"NORMAL".equalsIgnoreCase(alert.getEvent())) {

            AlertEntity alertEntity = new AlertEntity(
                    device,
                    alert.getEvent(),
                    alert.getSeverity().name(),
                    alert.getReason()
            );

            alertEntity.setTimestamp(alert.getTimestamp());

            alertRepository.save(alertEntity);

            webSocketHandler.broadcastAlert(alert);
        }

        // Broadcast monitoring data
        webSocketHandler.broadcastMonitoringData(data);
    }
}
