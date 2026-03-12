package io.trishul.flux.core.execution;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FluxLimiter {

    private long capacity = 100;
    private long refillRatePerSecond = 50;
    private final AtomicLong availableTokens = new AtomicLong(100);
    private long lastRefillTimestamp = System.currentTimeMillis();

    /**
     * The core 'Destroyer' logic.
     * Returns true if request is allowed, false if it must be destroyed.
     */
    public synchronized boolean tryAcquire() {
        refill();

        if (availableTokens.get() > 0) {
            availableTokens.decrementAndGet();
            return true;
        }

        log.warn("FluxLimiter: [DESTROYED] Token bucket empty. Request rejected.");
        return false;
    }

    /**
     * Logic to refill tokens based on time passed.
     */
    private void refill() {
        long now = System.currentTimeMillis();
        long deltaMillis = now - lastRefillTimestamp;

        if (deltaMillis > 1000) {
            long tokensToAdd = (deltaMillis / 1000) * refillRatePerSecond;
            if (tokensToAdd > 0) {
                long newValue = Math.min(capacity, availableTokens.get() + tokensToAdd);
                availableTokens.set(newValue);
                lastRefillTimestamp = now;
            }
        }
    }

    /**
     * The "Hook" for the AI.
     * The ReasoningEngine will call this to change system behavior.
     */
    public void updateRefillRate(long newRate) {
        log.info("FluxLimiter: AI is updating refill rate to {} req/sec", newRate);
        this.refillRatePerSecond = newRate;
    }
}