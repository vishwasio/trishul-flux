package io.trishul.flux.core.telemetry;

import java.time.Instant;

public record TelemetrySnapshot(
        Instant timestamp,
        double cpuUsage,
        long usedMemoryBytes,
        int liveThreads,
        SystemStatus status,
        long droppedRequests,
        long acceptedRequests
) {
    public enum SystemStatus {
        HEALTHY, STRESSED, CRITICAL
    }
}