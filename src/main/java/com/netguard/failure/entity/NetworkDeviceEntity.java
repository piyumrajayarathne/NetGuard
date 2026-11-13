package com.netguard.failure.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class NetworkDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceName;

    @Column(nullable = false)
    private String address;

    private Integer port;

    public NetworkDeviceEntity() {
    }

    public NetworkDeviceEntity(String deviceName,
                               String address,
                               Integer port) {
        this.deviceName = deviceName;
        this.address = address;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
