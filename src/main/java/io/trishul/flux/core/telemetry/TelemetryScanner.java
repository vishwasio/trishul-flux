package io.trishul.flux.core.telemetry;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryScanner {

    private final MeterRegistry meterRegistry;

    // Survival Scan: Runs every 10 seconds to keep RAM impact low
    @Scheduled(fixedRate = 10000)
    public void scanSystemPulse() {
        TelemetrySnapshot snapshot = captureSnapshot();

        log.info("Trishul-Flux Perception: [{}] | CPU: {}% | RAM: {}MB | Threads: {}",
                snapshot.status(),
                String.format("%.2f", snapshot.cpuUsage() * 100),
                String.format("%.2f", (double) snapshot.usedMemoryBytes() / 1024 / 1024),
                snapshot.liveThreads()
        );
    }

    /**
     * Captures a structured snapshot of the current system state.
     * This is the "Sense" that will be fed into the CHAKRA brain.
     */
    public TelemetrySnapshot captureSnapshot() {
        double cpu = fetchMetric("system.cpu.usage");
        long mem = fetchMetric("jvm.memory.used").longValue();
        int threads = fetchMetric("jvm.threads.live").intValue();

        // Perception Logic: Define state based on thresholds
        TelemetrySnapshot.SystemStatus status = TelemetrySnapshot.SystemStatus.HEALTHY;

        if (cpu > 0.85 || threads > 150) {
            status = TelemetrySnapshot.SystemStatus.CRITICAL;
        } else if (cpu > 0.60 || threads > 100) {
            status = TelemetrySnapshot.SystemStatus.STRESSED;
        }

        return new TelemetrySnapshot(
                Instant.now(),
                cpu,
                mem,
                threads,
                status
        );
    }

    private Double fetchMetric(String metricName) {
        try {
            var meter = meterRegistry.find(metricName).gauge();
            return (meter != null) ? meter.value() : 0.0;
        } catch (Exception e) {
            log.warn("Failed to fetch metric: {}", metricName);
            return 0.0;
        }
    }
}