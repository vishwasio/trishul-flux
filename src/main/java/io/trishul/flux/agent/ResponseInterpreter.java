package io.trishul.flux.agent;

import org.springframework.stereotype.Component;

@Component
public class ResponseInterpreter {

    public ActionPlan parse(String response) {
        if (response == null) return ActionPlan.MONITOR;

        String upperResponse = response.toUpperCase();

        if (upperResponse.contains("SCALE")) return ActionPlan.SCALE;
        if (upperResponse.contains("THROTTLE")) return ActionPlan.THROTTLE;
        if (upperResponse.contains("MONITOR")) return ActionPlan.MONITOR;

        return ActionPlan.MONITOR; // Fallback to safe state instead of UNKNOWN
    }
}