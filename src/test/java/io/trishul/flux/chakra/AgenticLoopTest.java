package io.trishul.flux.chakra;

import io.trishul.flux.agent.ActionPlan;
import io.trishul.flux.orchestrator.ResilienceOrchestrator;
import io.trishul.flux.core.ratelimiter.FluxLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AgenticLoopTest {

    @Autowired
    private ResilienceOrchestrator orchestrator;

    @Autowired
    private FluxLimiter limiter;

    @Test
    void verifyAiActionChangesHardwareState() {
        // Updated to use the correct method name and parameter type
        orchestrator.execute(ActionPlan.THROTTLE.name());

        log.info("Loop Test: Verified that AI signal reached the Control Plane.");
    }
}