package io.trishul.flux.core.telemetry;

import io.trishul.flux.agent.ActionPlan;
import io.trishul.flux.agent.DecisionEngine;
import io.trishul.flux.orchestrator.ResilienceOrchestrator;
import io.trishul.flux.infra.TrafficSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryScanner {

    private final DecisionEngine decisionEngine;
    private final ResilienceOrchestrator orchestrator;
    private final TrafficSimulator trafficSimulator;

    @Scheduled(fixedRate = 5000)
    public void scan() {
        TelemetrySnapshot snapshot = captureSnapshot();

        log.info("Resilience Engine - Perception: [{}] | CPU: {}% | RAM: {}MB | Success: {}",
                snapshot.status(),
                String.format("%.2f", snapshot.cpuUsage() * 100),
                snapshot.usedMemoryBytes() / (1024 * 1024),
                snapshot.acceptedRequests());

        // Corrected method name from your DecisionEngine.java
        ActionPlan action = decisionEngine.decideMitigation(snapshot);

        log.info("Resilience Engine - Cognition: AI decided to -> {}", action);

        // Corrected method name from your ResilienceOrchestrator.java
        orchestrator.execute(action.name());
    }

    public TelemetrySnapshot captureSnapshot() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getCpuLoad();
        if (cpuLoad < 0) cpuLoad = 0.0;

        long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int threads = Thread.activeCount();

        // Logic: If we are dropping requests, we are CRITICAL
        TelemetrySnapshot.SystemStatus status = (trafficSimulator.getRejectedCount() > 0) ?
                TelemetrySnapshot.SystemStatus.CRITICAL : TelemetrySnapshot.SystemStatus.HEALTHY;

        return new TelemetrySnapshot(
                Instant.now(),
                cpuLoad,
                usedMem,
                threads,
                status,
                trafficSimulator.getRejectedCount(),
                trafficSimulator.getSuccessfulCount()
        );
    }
}