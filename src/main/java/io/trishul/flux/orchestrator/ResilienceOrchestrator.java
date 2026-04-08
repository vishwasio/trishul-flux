package io.trishul.flux.orchestrator;

import io.trishul.flux.core.ratelimiter.FluxLimiter;
import io.trishul.flux.core.telemetry.TelemetryRepository;
import io.trishul.flux.infra.MockLoadBalancer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResilienceOrchestrator {

    private final FluxLimiter fluxLimiter;
    private final MockLoadBalancer loadBalancer;
    private final TelemetryRepository repository;

    public void execute(String action) {
        switch (action.toUpperCase()) {
            case "SCALE" -> scaleUp();
            case "THROTTLE" -> throttleDown();
            case "DRAIN" -> drainSystem();
            case "REBOOT" -> rebootSystem();
            default -> monitor();
        }
    }

    private void scaleUp() {
        log.info("[Orchestrator] Scaling infrastructure: Refill 100/s | Latency OFF");
        fluxLimiter.updateRefillRate(100);
        loadBalancer.setLatencyEnabled(false);
    }

    private void throttleDown() {
        log.info("[Orchestrator] Throttling: Refill 10/s | Latency ON");
        fluxLimiter.updateRefillRate(10);
        loadBalancer.setLatencyEnabled(true);
    }

    private void drainSystem() {
        log.info("[Orchestrator] EMERGENCY DRAIN: Dropping all traffic to clear queue");
        fluxLimiter.updateRefillRate(0); // Stop all requests
        loadBalancer.setLatencyEnabled(true);
    }

    private void rebootSystem() {
        log.warn("[Orchestrator] SYSTEM REBOOT: Purging memory and resetting baseline");
        // Clear the sliding window memory
        repository.clear();
        // Reset to healthy defaults
        fluxLimiter.updateRefillRate(50);
        loadBalancer.setLatencyEnabled(false);
    }

    private void monitor() {
        log.info("[Orchestrator] System stable. Maintaining current state.");
    }
}