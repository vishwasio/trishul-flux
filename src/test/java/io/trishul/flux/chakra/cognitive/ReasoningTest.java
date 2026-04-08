package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.agent.ActionPlan;
import io.trishul.flux.agent.DecisionEngine;
import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReasoningTest {

    @Autowired
    private DecisionEngine decisionEngine;

    @Test
    void verifyAiDecisionLogic() {
        // Mocking a Critical State (High CPU and Dropped Requests)
        TelemetrySnapshot criticalSnapshot = new TelemetrySnapshot(
                Instant.now(),
                0.95, // 95% CPU
                2048L * 1024 * 1024,
                50,
                TelemetrySnapshot.SystemStatus.CRITICAL,
                120, // 120 Dropped requests
                300
        );

        // Changed type from String to ActionPlan
        ActionPlan decision = decisionEngine.decideMitigation(criticalSnapshot);

        System.out.println("AI Decision for Critical State: " + decision);

        assertNotNull(decision);
        // Checking against the Enum values instead of String contains
        assertTrue(decision == ActionPlan.THROTTLE || decision == ActionPlan.SCALE,
                "AI should recommend THROTTLE or SCALE for a CRITICAL state");
    }
}