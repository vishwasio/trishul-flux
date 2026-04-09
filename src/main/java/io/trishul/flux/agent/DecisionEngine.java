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

    private final GeminiModelClient modelClient; // Instead of private final ModelClient modelClient; To use gemini instead of ollama 0.5B
    private final ResponseInterpreter parser;
    private int criticalStreak = 0;

    public ActionPlan decide(List<TelemetrySnapshot> history) {
        if (history.isEmpty()) return ActionPlan.MONITOR;

        TelemetrySnapshot current = history.get(0);

        // update escalation state based on CRITICAL status
        if (current.status() == TelemetrySnapshot.SystemStatus.CRITICAL) {
            criticalStreak++;
        } else {
            criticalStreak = 0;
        }

        String trend = calculateTrend(history);

        // selecting prompt based on the failure streak
        String prompt = (criticalStreak >= 3)
                ? constructEscalationPrompt(current, trend)
                : constructContextualPrompt(current, trend);

        if (criticalStreak >= 3) {
            log.warn("[DecisionEngine] System in persistent failure (Streak: {}). Forcing escalation.", criticalStreak);
        }

        String rawResponse = modelClient.chat(prompt);
        ActionPlan action = parser.parse(rawResponse);

        // HARD OVERRIDE
        // if the system is dying and the AI keeps suggesting THROTTLE, forcing a DRAIN.
        if (criticalStreak >= 5 && (action == ActionPlan.THROTTLE || action == ActionPlan.MONITOR)) {
            log.warn("[DecisionEngine] AI stuck in THROTTLE loop during crisis. Overriding to DRAIN.");
            criticalStreak = 0;
            return ActionPlan.DRAIN;
        }

        return action;
    }

    private String calculateTrend(List<TelemetrySnapshot> history) {
        if (history.size() < 2) return "STABLE";
        TelemetrySnapshot latest = history.get(0);
        TelemetrySnapshot oldest = history.get(history.size() - 1);

        String cpuDir = (latest.cpuUsage() > oldest.cpuUsage() + 5) ? "RISING" : "STABLE";
        String dropDir = (latest.droppedRequests() > oldest.droppedRequests()) ? "INCREASING" : "DECREASING";

        return String.format("CPU %s, DROPS %s", cpuDir, dropDir);
    }

    private String constructContextualPrompt(TelemetrySnapshot current, String trend) {
        return String.format(
                "[SRE-SIGNAL]\n" +
                        "STAT: %s\n" +
                        "CPU: %.1f%%\n" +
                        "DROP: %d\n" +
                        "TRND: %s\n" +
                        "CAP: 800\n" +
                        "EXEC: [MONITOR, SCALE, THROTTLE, DRAIN]",
                current.status(), current.cpuUsage(), current.droppedRequests(), trend
        );
    }

    private String constructEscalationPrompt(TelemetrySnapshot current, String trend) {
        return String.format(
                "[EMERGENCY-PROTOCOL]\n" +
                        "STREAK: %d FAILED CYCLES\n" +
                        "DROP: %d\n" +
                        "TREND: %s\n" +
                        "NOTICE: THROTTLE IS INEFFECTIVE. YOU MUST STOP TRAFFIC OR RESET.\n" +
                        "EXEC: [DRAIN, REBOOT]",
                criticalStreak, current.droppedRequests(), trend
        );
    }
}