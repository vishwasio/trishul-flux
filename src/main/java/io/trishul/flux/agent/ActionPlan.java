package io.trishul.flux.agent;

// Deterministic actions derived from AI reasoning.

public enum ActionPlan {
    SCALE,      // increase resources
    THROTTLE,   // reject traffic
    MONITOR,    // no change needed
    UNKNOWN     // hallucinations/ unrecognizable action
}