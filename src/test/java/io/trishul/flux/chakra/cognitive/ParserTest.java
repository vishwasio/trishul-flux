package io.trishul.flux.chakra.cognitive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private final ChakraResponseParser parser = new ChakraResponseParser();

    @Test
    void testParsingLogic() {
        assertEquals(ChakraAction.THROTTLE, parser.parse("THROTTLE"));
        assertEquals(ChakraAction.THROTTLE, parser.parse("  throttle.  ")); // Messy input
        assertEquals(ChakraAction.SCALE, parser.parse("I suggest we SCALE now")); // Sentence input
        assertEquals(ChakraAction.UNKNOWN, parser.parse("RELAX")); // Hallucination
    }
}