
package com.netguard.monitoring.model;

public class NetworkDevice {

    private String deviceName;
    private String address;
    private int port;

    public NetworkDevice(
            String deviceName,
            String address,
            int port) {

        this.deviceName = deviceName;
        this.address = address;
        this.port = port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
