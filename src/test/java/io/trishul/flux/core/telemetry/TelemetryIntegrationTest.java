package io.trishul.flux.core.telemetry;

import io.trishul.flux.core.limiter.LimiterMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TelemetryIntegrationTest {

    @Autowired
    private TelemetryScanner scanner;

    @Autowired
    private LimiterMetrics limiterMetrics;

    @Test
    void verifyCustomMetricsInSnapshot() {
        // simulate activity
        limiterMetrics.incrementAccepted();
        limiterMetrics.incrementAccepted();
        limiterMetrics.incrementDropped();

        // capture snapshot
        TelemetrySnapshot snapshot = scanner.captureSnapshot();

        // assertions
        assertNotNull(snapshot);
        assertEquals(2, snapshot.acceptedRequests(), "Accepted requests should be 2");
        assertEquals(1, snapshot.droppedRequests(), "Dropped requests should be 1");

        // verify Status logic (If dropped > 0, status should be CRITICAL per (my) logic)
        assertEquals(TelemetrySnapshot.SystemStatus.CRITICAL, snapshot.status());
    }
}