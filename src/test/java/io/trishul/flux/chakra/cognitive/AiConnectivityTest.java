package io.trishul.flux.chakra.cognitive;

import io.trishul.flux.agent.ModelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiConnectivityTest {

    @Autowired
    private ModelClient modelClient;

    @Test
    void verifyAiConnection() {
        String testPrompt = "Respond with the word 'READY' only.";
        String response = modelClient.chat(testPrompt);

        System.out.println("AI Response: " + response);

        assertNotNull(response);
        assertNotEquals("AI_OFFLINE", response);
        assertTrue(response.toUpperCase().contains("READY"));
    }
}