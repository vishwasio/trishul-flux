package io.trishul.flux.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResponseInterpreter {

    // Cleans and maps the AI string response to a ActionPlan
    public ActionPlan parse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return ActionPlan.UNKNOWN;
        }
        String clean = rawResponse.trim().toUpperCase().replaceAll("[^A-Z]", "");
        return switch (clean) {
            case String s when s.contains("SCALE") -> ActionPlan.SCALE;
            case String s when s.contains("THROTTLE") -> ActionPlan.THROTTLE;
            case String s when s.contains("MONITOR") -> ActionPlan.MONITOR;
            default -> {
                log.warn("Control Plane: AI returned unrecognizable action: {}", rawResponse);
                yield ActionPlan.UNKNOWN;
            }
        };
    }
}