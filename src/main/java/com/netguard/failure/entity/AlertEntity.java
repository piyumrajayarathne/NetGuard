package com.netguard.failure.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private NetworkDeviceEntity device;

    @Column(nullable = false)
    private String event;

    @Column(nullable = false)
    private String severity;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AlertEntity() {
    }

    public AlertEntity(
            NetworkDeviceEntity device,
            String event,
            String severity,
            String reason) {

        this.device = device;
        this.event = event;
        this.severity = severity;
        this.reason = reason;
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
