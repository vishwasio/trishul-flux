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
    private boolean latencyEnabled = false;

    public boolean handleRequest() {
        if (!circuitBreaker.canExecute()) {
            return false;
        }

        if (latencyEnabled) {
            try {
                // reduced to 20ms to allow scheduler heartbeats to remain consistent
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return limiter.tryAcquire();
    }

    public void setLatencyEnabled(boolean active) {
        this.latencyEnabled = active;
        log.info("[MockLoadBalancer] Latency injection: {}", active ? "ENABLED" : "DISABLED");
    }
}