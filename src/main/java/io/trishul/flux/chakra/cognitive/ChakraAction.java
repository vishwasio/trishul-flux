package io.trishul.flux.chakra.cognitive;

// Deterministic actions derived from AI reasoning.

public enum ChakraAction {
    SCALE,      // increase resources
    THROTTLE,   // reject traffic
    MONITOR,    // no change needed
    UNKNOWN     // hallucinations/ unrecognizable action
}