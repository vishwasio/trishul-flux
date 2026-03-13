package io.trishul.flux.core.execution;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class FluxCircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final int failureThreshold = 3;
    private long lastTripTimestamp = 0;
    private final long recoveryTimeout = 5000; // 5 seconds

    public boolean canExecute() {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastTripTimestamp > recoveryTimeout) {
                state = State.HALF_OPEN;
                log.info("CircuitBreaker: Attempting recovery (HALF_OPEN)");
                return true;
            }
            return false;
        }
        return true;
    }

    public void recordSuccess() {
        failureCount.set(0);
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            log.info("CircuitBreaker: System recovered (CLOSED)");
        }
    }

    public void recordFailure() {
        int failures = failureCount.incrementAndGet();
        if (failures >= failureThreshold && state != State.OPEN) {
            state = State.OPEN;
            lastTripTimestamp = System.currentTimeMillis();
            log.error("CircuitBreaker: Failure threshold reached. Circuit is now OPEN!");
        }
    }

    public State getState() { return state; }
}