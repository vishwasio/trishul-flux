package io.trishul.flux.infra;

import io.trishul.flux.core.circuitbreaker.FluxCircuitBreaker;
import io.trishul.flux.core.ratelimiter.FluxLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockLoadBalancer {

    private final FluxLimiter limiter;
    private final FluxCircuitBreaker circuitBreaker;
    private boolean slowMode = false;

    public boolean handleRequest() {
        // circuit breaker check
        if (!circuitBreaker.canExecute()) {
            return false;
        }

        // simulate latency if slowMode is enabled
        if (slowMode) {
            try {
                // 200ms delay to simulate upstream pressure
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return limiter.tryAcquire();
    }

    public void toggleSlowMode(boolean active) {
        this.slowMode = active;
        log.info("Control Plane: Latency injection is now {}", active ? "ENABLED" : "DISABLED");
    }
}