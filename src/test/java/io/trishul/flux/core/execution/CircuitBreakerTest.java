package io.trishul.flux.core.execution;

import io.trishul.flux.core.circuitbreaker.FluxCircuitBreaker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {

    @Test
    void verifyStateTransitions() {
        FluxCircuitBreaker cb = new FluxCircuitBreaker();

        // Trigger failures
        cb.recordFailure();
        cb.recordFailure();
        cb.recordFailure();

        assertEquals(FluxCircuitBreaker.State.OPEN, cb.getState());
        assertFalse(cb.canExecute(), "Should block execution when OPEN");

        // Success after failure shouldn't work while OPEN [must wait for timeout]
        cb.recordSuccess();
        assertEquals(FluxCircuitBreaker.State.OPEN, cb.getState());
    }
}