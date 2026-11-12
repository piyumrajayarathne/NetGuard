
package com.netguard.monitoring.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkReachabilityServiceTest {

    private final NetworkReachabilityService service =
            new NetworkReachabilityService();

    @Test
    void shouldRejectEmptyDeviceAddress() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("", 3000)
        );
    }

    @Test
    void shouldRejectNullDeviceAddress() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability(null, 3000)
        );
    }

    @Test
    void shouldRejectInvalidTimeout() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("8.8.8.8", 0)
        );
    }
}
