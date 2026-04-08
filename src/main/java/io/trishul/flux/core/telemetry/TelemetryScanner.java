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

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryScanner {

    private final DecisionEngine decisionEngine;
    private final ResilienceOrchestrator orchestrator;
    private final TrafficSimulator trafficSimulator;
    private final TelemetryRepository repository;

    @Scheduled(fixedRate = 5000)
    public void scan() {
        TelemetrySnapshot snapshot = captureSnapshot();

        // Push snapshot to sliding window memory
        repository.addSnapshot(snapshot);

        log.info("Perception: [{}] | History Depth: {}/10 | CPU: {}% | Drops: {}",
                snapshot.status(),
                repository.getRecentCount(),
                String.format("%.2f", snapshot.cpuUsage()),
                snapshot.droppedRequests());

        ActionPlan action = decisionEngine.decide(snapshot);

        log.info("Cognition: AI decision -> {}", action);
        orchestrator.execute(action.name());
    }

    protected TelemetrySnapshot captureSnapshot() {
        // MXBean Logic for system metrics
        double cpu = com.sun.management.OperatingSystemMXBean.class.isInstance(
                java.lang.management.ManagementFactory.getOperatingSystemMXBean())
                ? ((com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory
                .getOperatingSystemMXBean()).getCpuLoad() * 100
                : 0.0;

        long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int threads = Thread.activeCount();

        TelemetrySnapshot.SystemStatus status = TelemetrySnapshot.SystemStatus.HEALTHY;
        if (cpu > 85 || trafficSimulator.getRejectedCount() > 50) {
            status = TelemetrySnapshot.SystemStatus.CRITICAL;
        } else if (cpu > 60 || trafficSimulator.getRejectedCount() > 0) {
            status = TelemetrySnapshot.SystemStatus.STRESSED;
        }

        return new TelemetrySnapshot(
                Instant.now(),
                cpu,
                mem,
                threads,
                status,
                trafficSimulator.getRejectedCount(),
                trafficSimulator.getSuccessfulCount()
        );
    }
}