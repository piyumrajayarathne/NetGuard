
package com.netguard.monitoring.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PingReachabilityServiceTest {

    private final PingReachabilityService service =
            new PingReachabilityService();

    @Test
    void shouldRejectNullDevice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability(null, 1000, 3)
        );
    }

    @Test
    void shouldRejectBlankDevice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability(" ", 1000, 3)
        );
    }

    @Test
    void shouldRejectZeroTimeout() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("8.8.8.8", 0, 3)
        );
    }

    @Test
    void shouldRejectNegativeTimeout() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("8.8.8.8", -100, 3)
        );
    }

    @Test
    void shouldRejectZeroAttempts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("8.8.8.8", 1000, 0)
        );
    }

    @Test
    void shouldRejectNegativeAttempts() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkReachability("8.8.8.8", 1000, -1)
        );
    }
}
