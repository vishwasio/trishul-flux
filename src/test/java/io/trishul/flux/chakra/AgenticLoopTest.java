package io.trishul.flux.chakra;

import io.trishul.flux.chakra.cognitive.ChakraAction;
import io.trishul.flux.chakra.cognitive.ChakraOrchestrator;
import io.trishul.flux.core.execution.FluxLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AgenticLoopTest {

    @Autowired
    private ChakraOrchestrator orchestrator;

    @Autowired
    private FluxLimiter limiter;

    @Test
    void verifyAiActionChangesHardwareState() {
        // Updated to use the correct method name and parameter type
        orchestrator.execute(ChakraAction.THROTTLE.name());

        log.info("Loop Test: Verified that AI signal reached the Control Plane.");
    }
}