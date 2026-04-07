package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.core.execution.FluxLimiter;
import io.trishul.flux.core.execution.MockLoadBalancer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChakraOrchestrator {

    private final FluxLimiter limiter;
    private final MockLoadBalancer loadBalancer;

    public void execute(String decision) {
        switch (decision.toUpperCase()) {
            case "THROTTLE":
                log.info("Control Plane: [ACTION] Applying strict rate limiting...");
                limiter.updateRefillRate(10);
                // optionally trigger latency to simulate a struggling node
                loadBalancer.toggleSlowMode(true);
                break;

            case "SCALE":
                log.info("Control Plane: [ACTION] Scaling up capacity...");
                limiter.updateRefillRate(100);
                loadBalancer.toggleSlowMode(false);
                break;

            case "MONITOR":
            default:
                log.info("Control Plane: [ACTION] Maintaining equilibrium. No change needed.");
                break;
        }
    }
}