package io.trishul.flux.chakra.cognitive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChakraResponseParser {

    /**
     * Cleans and maps the AI string response to a ChakraAction.
     */
    public ChakraAction parse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return ChakraAction.UNKNOWN;
        }

        // Clean the string: Uppercase, remove periods/whitespace
        String clean = rawResponse.trim().toUpperCase().replaceAll("[^A-Z]", "");

        return switch (clean) {
            case String s when s.contains("SCALE") -> ChakraAction.SCALE;
            case String s when s.contains("THROTTLE") -> ChakraAction.THROTTLE;
            case String s when s.contains("MONITOR") -> ChakraAction.MONITOR;
            default -> {
                log.warn("Chakra: AI returned unrecognizable action: {}", rawResponse);
                yield ChakraAction.UNKNOWN;
            }
        };
    }
}