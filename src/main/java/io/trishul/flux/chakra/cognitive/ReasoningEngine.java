package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReasoningEngine {

    private final OllamaClient ollamaClient;

    /**
     * Translates a system snapshot into an AI decision.
     */
    public String decideMitigation(TelemetrySnapshot snapshot) {
        String prompt = constructPrompt(snapshot);

        log.info("Chakra: Reasoning about system state [{}...] ", snapshot.status());
        return ollamaClient.chat(prompt);
    }

    private String constructPrompt(TelemetrySnapshot snapshot) {
        return String.format(
                "System Status: %s. Metrics: CPU %d%%, RAM %dMB, Dropped Requests: %d. " +
                        "Instruction: Based on this, provide a single word action: [SCALE, THROTTLE, or MONITOR]. " +
                        "Response must be one word only.",
                snapshot.status(),
                (int)(snapshot.cpuUsage() * 100),
                snapshot.usedMemoryBytes() / 1024 / 1024,
                snapshot.droppedRequests()
        );
    }
}