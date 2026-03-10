package io.trishul.flux.core.telemetry;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryScanner {

    private final MeterRegistry meterRegistry;

    // Survival Scan: Runs every 10 seconds to keep RAM impact low
    @Scheduled(fixedRate = 10000)
    public void scanSystemPulse() {
        Map<String, Double> metrics = new HashMap<>();

        // 1. CPU Usage (System)
        metrics.put("system.cpu", fetchMetric("system.cpu.usage"));

        // 2. Memory Usage (JVM Heap)
        metrics.put("jvm.memory.used", fetchMetric("jvm.memory.used"));

        // 3. Thread Count
        metrics.put("jvm.threads.live", fetchMetric("jvm.threads.live"));

        log.info("Trishul-Flux Perception: CPU: {}% | RAM: {}MB | Threads: {}",
                String.format("%.2f", metrics.get("system.cpu") * 100),
                String.format("%.2f", metrics.get("jvm.memory.used") / 1024 / 1024),
                metrics.get("jvm.threads.live").intValue()
        );

        // This data will eventually be fed into the "Data Model" (I#3M2-GitHub)
    }

    private Double fetchMetric(String metricName) {
        try {
            return meterRegistry.get(metricName).gauge().value();
        } catch (Exception e) {
            return 0.0; // Fallback if metric isn't initialized yet
        }
    }
}