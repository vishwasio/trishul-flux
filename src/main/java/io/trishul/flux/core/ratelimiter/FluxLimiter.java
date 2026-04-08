package io.trishul.flux.core.ratelimiter;

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

    public synchronized boolean tryAcquire() {
        refill();
        if (availableTokens.get() > 0) {
            availableTokens.decrementAndGet();
            return true;
        }
        return false;
    }

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

    public void updateRefillRate(long newRate) {
        log.info("Resilience Engine: Control Plane updating refill rate to {} req/sec", newRate);
        this.refillRatePerSecond = newRate;
    }
}