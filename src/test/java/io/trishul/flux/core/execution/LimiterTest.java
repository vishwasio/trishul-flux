package io.trishul.flux.core.execution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LimiterTest {

    @Test
    void verifyTokenBucketDestruction() {
        FluxLimiter limiter = new FluxLimiter();
        int allowed = 0;
        int destroyed = 0;

        // Simulate 150 rapid requests
        for (int i = 0; i < 150; i++) {
            if (limiter.tryAcquire()) {
                allowed++;
            } else {
                destroyed++;
            }
        }

        System.out.println("Allowed: " + allowed + " | Destroyed: " + destroyed);

        assertEquals(100, allowed, "Should allow exactly 100 tokens (the capacity)");
        assertEquals(50, destroyed, "Should destroy exactly 50 overflowing requests");
    }
}