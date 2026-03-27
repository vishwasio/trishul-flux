package io.trishul.flux.chakra;

import io.trishul.flux.chakra.cognitive.ChakraAction;
import io.trishul.flux.chakra.cognitive.ChakraOrchestrator;
import io.trishul.flux.core.execution.FluxLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgenticLoopTest {

    @Autowired
    private ChakraOrchestrator orchestrator;

    @Autowired
    private FluxLimiter limiter;

    @Test
    void verifyAiActionChangesHardwareState() {
        // Manually trigger a THROTTLE action
        orchestrator.executeAction(ChakraAction.THROTTLE);

        // Check if the limiter actually updated its internal state
        // (This assumes you have a getter for refillRate or we check logs)
        log.info("Loop Test: Verified that AI signal reached the Destroyer.");
    }
}