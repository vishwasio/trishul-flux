package io.trishul.flux.chakra.cognitive;

/**
 * Deterministic actions derived from AI reasoning.
 */
public enum ChakraAction {
    SCALE,      // Increase resources/limits
    THROTTLE,   // Decrease limits/reject traffic
    MONITOR,    // Stay the course, no change needed
    UNKNOWN     // Fallback for hallucinated responses
}