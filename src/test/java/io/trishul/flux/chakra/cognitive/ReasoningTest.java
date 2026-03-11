package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReasoningTest {

    @Autowired
    private ReasoningEngine reasoningEngine;

    @Test
    void verifyAiDecisionLogic() {
        // Mock a Critical State (High CPU and Dropped Requests)
        TelemetrySnapshot criticalSnapshot = new TelemetrySnapshot(
                Instant.now(),
                0.95, // 95% CPU
                2048L * 1024 * 1024,
                50,
                TelemetrySnapshot.SystemStatus.CRITICAL,
                120, // 120 Dropped requests
                300
        );

        // Updated: Changed type from String to ChakraAction
        ChakraAction decision = reasoningEngine.decideMitigation(criticalSnapshot);

        System.out.println("AI Decision for Critical State: " + decision);

        assertNotNull(decision);
        // Updated: Checking against the Enum values instead of String contains
        assertTrue(decision == ChakraAction.THROTTLE || decision == ChakraAction.SCALE,
                "AI should recommend THROTTLE or SCALE for a CRITICAL state");
    }
}