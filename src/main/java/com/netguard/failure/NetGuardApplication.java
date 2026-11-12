
package com.netguard.failure;

import com.netguard.monitoring.service.PeriodicMonitoringService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.netguard")
public class NetGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetGuardApplication.class, args);
    }

    @Bean
    public CommandLineRunner startMonitoring(
            PeriodicMonitoringService periodicMonitoringService) {

        return args -> periodicMonitoringService.startMonitoring();
    }
}
