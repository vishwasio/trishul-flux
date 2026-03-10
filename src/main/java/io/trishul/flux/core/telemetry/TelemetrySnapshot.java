package io.trishul.flux.core.telemetry;

import java.time.Instant;

/**
 * Immutable snapshot of the system state for CHAKRA reasoning.
 */
public record TelemetrySnapshot(
        Instant timestamp,
        double cpuUsage,
        long usedMemoryBytes,
        int liveThreads,
        SystemStatus status
) {
    public enum SystemStatus {
        HEALTHY, STRESSED, CRITICAL
    }
}