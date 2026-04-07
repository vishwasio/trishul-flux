package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.core.execution.FluxLimiter;
import io.trishul.flux.core.execution.FluxCircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChakraOrchestrator {

    private final FluxLimiter limiter;
    // private final FluxCircuitBreaker circuitBreaker;

    // Executes the AI's decision on the physical infrastructure
    public void executeAction(ChakraAction action) {
        switch (action) {
            case THROTTLE -> {
                log.info("Control Plane: [ACTION] Applying strict rate limiting...");
                limiter.updateRefillRate(10);
            }
            case SCALE -> {
                log.info("Control Plane: [ACTION] Scaling up capacity...");
                limiter.updateRefillRate(100);
            }
            case MONITOR -> {
                log.info("Control Plane: [ACTION] Maintaining equilibrium. No change needed.");
            }
            default -> log.warn("Control Plane: [ACTION] Unknown action received: {}", action);
        }
    }
}