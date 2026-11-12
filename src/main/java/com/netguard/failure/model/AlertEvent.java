package com.netguard.failure.model;

import java.time.LocalDateTime;

public class AlertEvent {

    private String device;
    private String event;
    private AlertSeverity severity;
    private String reason;
    private LocalDateTime timestamp;

    public AlertEvent() {
    }

    public AlertEvent(String device,
                      String event,
                      AlertSeverity severity,
                      String reason) {

        this.device = device;
        this.event = event;
        this.severity = severity;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
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

    @Override
    public String toString() {
        return "AlertEvent{" +
                "device='" + device + '\'' +
                ", event='" + event + '\'' +
                ", severity=" + severity +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}