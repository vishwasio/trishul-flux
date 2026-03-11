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

    @Scheduled(fixedRate = 10000)
    public void scanSystemPulse() {
        TelemetrySnapshot snapshot = captureSnapshot();

        log.info("Trishul-Flux Perception: [{}] | CPU: {}% | RAM: {}MB | Dropped: {} | Accepted: {}",
                snapshot.status(),
                String.format("%.2f", snapshot.cpuUsage() * 100),
                String.format("%.2f", (double) snapshot.usedMemoryBytes() / 1024 / 1024),
                snapshot.droppedRequests(),
                snapshot.acceptedRequests()
        );
    }

    public TelemetrySnapshot captureSnapshot() {
        double cpu = fetchMetric("system.cpu.usage");
        long mem = fetchMetric("jvm.memory.used").longValue();
        int threads = fetchMetric("jvm.threads.live").intValue();

        // Fetch our custom Trishul metrics
        long dropped = fetchCounter("trishul.limiter.requests", "action", "dropped");
        long accepted = fetchCounter("trishul.limiter.requests", "action", "accepted");

        TelemetrySnapshot.SystemStatus status = TelemetrySnapshot.SystemStatus.HEALTHY;

        // Logic Update: If we are dropping requests, we aren't fully Healthy
        if (cpu > 0.85 || dropped > 0) {
            status = TelemetrySnapshot.SystemStatus.CRITICAL;
        } else if (cpu > 0.60) {
            status = TelemetrySnapshot.SystemStatus.STRESSED;
        }

        return new TelemetrySnapshot(
                Instant.now(),
                cpu,
                mem,
                threads,
                status,
                dropped,
                accepted
        );
    }

    private Double fetchMetric(String metricName) {
        try {
            var meter = meterRegistry.find(metricName).gauge();
            return (meter != null) ? meter.value() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private long fetchCounter(String name, String tagKey, String tagValue) {
        try {
            var counter = meterRegistry.find(name).tag(tagKey, tagValue).counter();
            return (counter != null) ? (long) counter.count() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}