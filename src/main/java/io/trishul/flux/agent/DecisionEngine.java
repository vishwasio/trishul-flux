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

    public ActionPlan decide(TelemetrySnapshot snapshot) {
        String prompt = constructPrompt(snapshot);

        log.info("[DecisionEngine] Reasoning about system state [{}]", snapshot.status());
        String rawResponse = modelClient.chat(prompt);

        ActionPlan action = parser.parse(rawResponse);
        log.info("[DecisionEngine] Decision formulated -> {}", action);

        return action;
    }

    private String constructPrompt(TelemetrySnapshot snapshot) {
        return String.format(
                "System Status: %s. Metrics: CPU %.2f%%, RAM %dMB, Dropped: %d, Success: %d. " +
                        "Instruction: Provide a one-word command: [SCALE, THROTTLE, or MONITOR].",
                snapshot.status(),
                snapshot.cpuUsage(),
                snapshot.usedMemoryBytes() / (1024 * 1024),
                snapshot.droppedRequests(),
                snapshot.acceptedRequests()
        );
    }
}