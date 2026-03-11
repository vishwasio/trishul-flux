package io.trishul.flux.chakra.cognitive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiConnectivityTest {

    @Autowired
    private OllamaClient ollamaClient;

    @Test
    void verifyAiConnection() {
        String testPrompt = "Respond with the word 'READY' only.";
        String response = ollamaClient.chat(testPrompt);

        System.out.println("AI Response: " + response);

        assertNotNull(response);
        assertNotEquals("AI_OFFLINE", response);
        assertTrue(response.toUpperCase().contains("READY"));
    }
}