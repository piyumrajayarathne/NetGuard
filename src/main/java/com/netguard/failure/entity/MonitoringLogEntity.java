package com.netguard.failure.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoring_logs")
public class MonitoringLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private NetworkDeviceEntity device;

    @Column(nullable = false)
    private String status;

    private Double latency;

    private Double packetLoss;

    private Double responseTime;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public MonitoringLogEntity() {
    }

    public MonitoringLogEntity(
            NetworkDeviceEntity device,
            String status,
            Double latency,
            Double packetLoss,
            Double responseTime) {

        this.device = device;
        this.status = status;
        this.latency = latency;
        this.packetLoss = packetLoss;
        this.responseTime = responseTime;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    public void setTimestampBeforeSaving() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public NetworkDeviceEntity getDevice() {
        return device;
    }

    public void setDevice(NetworkDeviceEntity device) {
        this.device = device;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }

    public Double getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(Double packetLoss) {
        this.packetLoss = packetLoss;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
