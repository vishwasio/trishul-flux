package io.trishul.flux.core.telemetry;

import io.trishul.flux.core.limiter.LimiterMetrics;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TelemetryStressTest {

    @Autowired
    private TelemetryScanner scanner;

    @Autowired
    private LimiterMetrics limiterMetrics;

    @Test
    void performHighConcurrencyStressTest() throws InterruptedException {
        int totalRequests = 2000;
        log.info("Starting Stress Test: Simulating {} requests via Virtual Threads...", totalRequests);

        // Using Virtual Threads to simulate high concurrency
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < totalRequests; i++) {
                executor.submit(() -> {
                    // Randomly decide if a request is "Accepted" or "Dropped"
                    if (Math.random() > 0.7) {
                        limiterMetrics.incrementDropped();
                    } else {
                        limiterMetrics.incrementAccepted();
                    }
                });
            }
        } // Executor auto-closes and waits for all virtual threads to finish

        // Trigger the scanner to capture the aftermath
        TelemetrySnapshot snapshot = scanner.captureSnapshot();

        log.info("--- STRESS TEST RESULTS ---");
        log.info("Total Simulated: {}", totalRequests);
        log.info("Scanner Reported Accepted: {}", snapshot.acceptedRequests());
        log.info("Scanner Reported Dropped: {}", snapshot.droppedRequests());
        log.info("Final System Status: [{}]", snapshot.status());
        log.info("---------------------------");

        // Assertions
        assertEquals(totalRequests, snapshot.acceptedRequests() + snapshot.droppedRequests(),
                "Total requests tracked must match total requests sent");

        if (snapshot.droppedRequests() > 0) {
            assertEquals(TelemetrySnapshot.SystemStatus.CRITICAL, snapshot.status(),
                    "System must report CRITICAL when drops are detected");
        }
    }
}