package com.netguard.monitoring.service;

import com.netguard.failure.model.MonitoringData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PingReachabilityService {

    private static final Pattern WINDOWS_LINUX_TIME =
            Pattern.compile("time[=<]\\s*(\\d+(?:\\.\\d+)?)\\s*ms",
                    Pattern.CASE_INSENSITIVE);

    public MonitoringData checkReachability(
            String device,
            int timeoutMillis,
            int attempts) {

        // Validate inputs
        if (device == null || device.isBlank()) {
            throw new IllegalArgumentException(
                    "Device address cannot be empty");
        }

        if (timeoutMillis <= 0 || attempts <= 0) {
            throw new IllegalArgumentException(
                    "Timeout and attempts must be greater than zero");
        }

        int successfulPings = 0;
        double totalLatency = 0;

        for (int i = 0; i < attempts; i++) {

            try {
                Double latency = executePing(device, timeoutMillis);

                if (latency != null) {
                    successfulPings++;
                    totalLatency += latency;
                }

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Unable to execute ping command", e);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Ping monitoring was interrupted", e);
            }
        }

        int failedPings = attempts - successfulPings;

        double packetLoss =
                (failedPings * 100.0) / attempts;

        Double averageLatency = null;

        if (successfulPings > 0) {
            averageLatency = totalLatency / successfulPings;
        }

        String status = successfulPings > 0
                ? "REACHABLE"
                : "UNREACHABLE";

        return new MonitoringData(
                device,
                status,
                averageLatency,
                packetLoss
        );
    }

    private Double executePing(
            String device,
            int timeoutMillis)
            throws IOException, InterruptedException {

        boolean windows = System.getProperty("os.name")
                .toLowerCase()
                .contains("win");

        ProcessBuilder processBuilder;

        if (windows) {

            processBuilder = new ProcessBuilder(
                    "ping",
                    "-n", "1",
                    "-w", String.valueOf(timeoutMillis),
                    device
            );

        } else {

            int timeoutSeconds =
                    (int) Math.ceil(timeoutMillis / 1000.0);

            processBuilder = new ProcessBuilder(
                    "ping",
                    "-c", "1",
                    "-W", String.valueOf(timeoutSeconds),
                    device
            );
        }

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try {
            boolean completed = process.waitFor(
                    timeoutMillis + 2000L,
                    TimeUnit.MILLISECONDS
            );

            if (!completed) {
                process.destroyForcibly();
                process.waitFor();
                return null;
            }

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            if (process.exitValue() != 0) {
                return null;
            }

            Matcher matcher =
                    WINDOWS_LINUX_TIME.matcher(output);

            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }

            // Windows can report a response time below 1 ms.
            if (output.matches("(?is).*time<1ms.*")) {
                return 0.5;
            }

            return null;

        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}