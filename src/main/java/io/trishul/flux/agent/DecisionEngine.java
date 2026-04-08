package io.trishul.flux.agent;

import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final ModelClient modelClient;
    private final ResponseInterpreter parser;

    // system snapshot into deterministic action.
    public ActionPlan decideMitigation(TelemetrySnapshot snapshot) {
        String prompt = constructPrompt(snapshot);

        log.info("Control Plane: Reasoning about system state [{}] ", snapshot.status());
        String rawResponse = modelClient.chat(prompt);

        ActionPlan action = parser.parse(rawResponse);
        log.info("Control Plane: Decision formulated -> {}", action);

        return action;
    }

    private String constructPrompt(TelemetrySnapshot snapshot) {
        return String.format(
                "System Status: %s. Metrics: CPU %d%%, RAM %dMB, Dropped: %d. " +
                        "Instruction: Provide a one-word command: [SCALE, THROTTLE, or MONITOR].",
                snapshot.status(),
                (int)(snapshot.cpuUsage() * 100),
                snapshot.usedMemoryBytes() / 1024 / 1024,
                snapshot.droppedRequests()
        );
    }
}