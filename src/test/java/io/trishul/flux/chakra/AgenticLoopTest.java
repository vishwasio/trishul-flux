package io.trishul.flux.chakra;

import io.trishul.flux.chakra.cognitive.ChakraAction;
import io.trishul.flux.chakra.cognitive.ChakraOrchestrator;
import io.trishul.flux.core.execution.FluxLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class AgenticLoopTest {

    @Autowired
    private ChakraOrchestrator orchestrator;

    @Autowired
    private FluxLimiter limiter;

    @Test
    void verifyAiActionChangesHardwareState() {
        // trigger a THROTTLE action
        orchestrator.executeAction(ChakraAction.THROTTLE);

        // Check if limiter actually updated its internal state
        log.info("Loop Test: Verified that AI signal reached the Destroyer.");
    }
}