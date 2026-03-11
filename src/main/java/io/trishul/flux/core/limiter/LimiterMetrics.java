package io.trishul.flux.core.limiter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class LimiterMetrics {

    private final Counter droppedRequestsCounter;
    private final Counter acceptedRequestsCounter;

    public LimiterMetrics(MeterRegistry registry) {
        // Metric: trishul.limiter.requests.dropped
        this.droppedRequestsCounter = Counter.builder("trishul.limiter.requests")
                .tag("action", "dropped")
                .description("Number of requests rejected by the rate limiter")
                .register(registry);

        // Metric: trishul.limiter.requests.accepted
        this.acceptedRequestsCounter = Counter.builder("trishul.limiter.requests")
                .tag("action", "accepted")
                .description("Number of requests permitted by the rate limiter")
                .register(registry);
    }

    public void incrementDropped() {
        droppedRequestsCounter.increment();
    }

    public void incrementAccepted() {
        acceptedRequestsCounter.increment();
    }
}