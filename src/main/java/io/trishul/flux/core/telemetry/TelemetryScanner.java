package io.trishul.flux.core.telemetry;

import io.trishul.flux.chakra.cognitive.ChakraAction;
import io.trishul.flux.chakra.cognitive.ChakraOrchestrator;
import io.trishul.flux.chakra.cognitive.ReasoningEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
//import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryScanner {

    private final ReasoningEngine reasoningEngine;
    private final ChakraOrchestrator orchestrator;

    @Scheduled(fixedRate = 5000)
    public void scanSystem() {
        // 1. Perception
        TelemetrySnapshot snapshot = captureSnapshot();

        log.info("Trishul-Flux Perception: [{}] | CPU: {}% | RAM: {}MB | Threads: {}",
                snapshot.status(),
                String.format("%.2f", snapshot.cpuUsage() * 100),
                snapshot.usedMemoryBytes() / (1024 * 1024),
                snapshot.liveThreads());

        // 2. Cognition
        ChakraAction decision = reasoningEngine.decideMitigation(snapshot);
        log.info("Trishul-Flux Cognition: AI decided to -> {}", decision);

        // 3. Execution
        orchestrator.executeAction(decision);
    }

    private TelemetrySnapshot captureSnapshot() {
        // Cast to the specific Sun implementation to get actual CPU load on Windows
        com.sun.management.OperatingSystemMXBean sunOsBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        // getCpuLoad() returns 0.0 to 1.0 (e.g., 0.85 = 85%)
        double cpu = sunOsBean.getCpuLoad();

        // If it's still negative (initialization), default to 0
        if (cpu < 0) cpu = 0.02;

        long mem = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMem = memoryBean.getHeapMemoryUsage().getMax();
        int threads = threadBean.getThreadCount();

        // Logical Thresholds
        TelemetrySnapshot.SystemStatus status = TelemetrySnapshot.SystemStatus.HEALTHY;

        if (cpu > 0.85 || (double) mem / maxMem > 0.9) {
            status = TelemetrySnapshot.SystemStatus.CRITICAL;
        } else if (cpu > 0.6) {
            status = TelemetrySnapshot.SystemStatus.STRESSED;
        }

        return new TelemetrySnapshot(
                Instant.now(),
                cpu,
                mem,
                threads,
                status,
                0,
                0
        );
    }
}