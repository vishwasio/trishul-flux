package io.trishul.flux.agent;

import io.trishul.flux.core.telemetry.TelemetrySnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final ModelClient modelClient;
    private final ResponseInterpreter parser;

    public ActionPlan decide(List<TelemetrySnapshot> history) {
        if (history.isEmpty()) return ActionPlan.MONITOR;

        TelemetrySnapshot current = history.get(0);
        String trendAnalysis = calculateTrend(history);

        String prompt = constructContextualPrompt(current, trendAnalysis);

        log.info("[DecisionEngine] Analyzing trend: {}", trendAnalysis);
        String rawResponse = modelClient.chat(prompt);

        ActionPlan action = parser.parse(rawResponse);
        log.info("[DecisionEngine] AI Reasoning Result -> {}", action);

        return action;
    }

    private String calculateTrend(List<TelemetrySnapshot> history) {
        if (history.size() < 2) return "STABLE (Insufficient data)";

        TelemetrySnapshot latest = history.get(0);
        TelemetrySnapshot oldest = history.get(history.size() - 1);

        double cpuDelta = latest.cpuUsage() - oldest.cpuUsage();
        long dropDelta = latest.droppedRequests() - oldest.droppedRequests();

        String cpuDir = cpuDelta > 5 ? "RISING" : (cpuDelta < -5 ? "FALLING" : "STABLE");
        String dropDir = dropDelta > 0 ? "INCREASING" : "STABLE";

        return String.format("CPU is %s, Rejection rate is %s", cpuDir, dropDir);
    }

    private String constructContextualPrompt(TelemetrySnapshot current, String trend) {
        return String.format(
                "Context: %s. Current Status: %s. Metrics: CPU %.2f%%, Dropped: %d.\n" +
                        "Instruction: Analyze the trend and respond with ONE WORD: [SCALE, THROTTLE, or MONITOR].",
                trend, current.status(), current.cpuUsage(), current.droppedRequests()
        );
    }
}