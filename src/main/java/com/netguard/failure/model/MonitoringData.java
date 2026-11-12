package com.netguard.failure.model;

public class MonitoringData {

    private String device;
    private String status;
    private Double latency;
    private Double packetLoss;

    public MonitoringData() {
    }

    public MonitoringData(String device, String status,
                          Double latency, Double packetLoss) {
        this.device = device;
        this.status = status;
        this.latency = latency;
        this.packetLoss = packetLoss;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
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

    @Override
    public String toString() {
        return "MonitoringData{" +
                "device='" + device + '\'' +
                ", status='" + status + '\'' +
                ", latency=" + latency +
                ", packetLoss=" + packetLoss +
                '}';
    }
}