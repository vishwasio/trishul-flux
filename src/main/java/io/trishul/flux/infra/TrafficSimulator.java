package io.trishul.flux.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficSimulator {

    private final MockLoadBalancer loadBalancer;
    private final AtomicLong successfulHits = new AtomicLong(0);
    private final AtomicLong rejectedHits = new AtomicLong(0);

    @Scheduled(fixedRate = 1000)
    public void generateTraffic() {
        // Randomize traffic between 1 and 500 requests per second
        // This forces the AI to handle both "Quiet" and "Overload" periods
        int burstSize = (int) (Math.random() * 500) + 1;
        long currentSuccess = 0;
        long currentFailure = 0;

        for (int i = 0; i < burstSize; i++) {
            if (loadBalancer.handleRequest()) {
                successfulHits.incrementAndGet();
                currentSuccess++;
            } else {
                rejectedHits.incrementAndGet();
                currentFailure++;
            }
        }

        double successRate = (burstSize > 0) ? ((double) currentSuccess / burstSize) * 100 : 100;
        log.info("Simulator: [DYNAMIC BURST] Size: {} | Success: {} | Dropped: {} | Rate: {}%",
                burstSize, currentSuccess, currentFailure, String.format("%.2f", successRate));
    }

    public long getSuccessfulCount() {
        return successfulHits.get();
    }

    public long getRejectedCount() {
        return rejectedHits.get();
    }
}