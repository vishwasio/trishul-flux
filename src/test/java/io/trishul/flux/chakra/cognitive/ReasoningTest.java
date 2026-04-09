package io.trishul.flux.agent;

import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ReasoningTest {

    @Mock
    private ModelClient modelClient;

    @Mock
    private ResponseInterpreter parser;

    @InjectMocks
    private DecisionEngine decisionEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDecideActionWithHistory() {
        // mock history window with 2 snapshots to allow trend calculation
        TelemetrySnapshot snapshot = new TelemetrySnapshot(
                Instant.now(), 85.0, 1024L, 10,
                TelemetrySnapshot.SystemStatus.CRITICAL, 50, 100
        );
        List<TelemetrySnapshot> history = List.of(snapshot, snapshot);

        // mock AI response
        when(modelClient.chat(anyString())).thenReturn("THROTTLE");
        when(parser.parse("THROTTLE")).thenReturn(ActionPlan.THROTTLE);

        ActionPlan result = decisionEngine.decide(history);

        assertEquals(ActionPlan.THROTTLE, result);
    }
}