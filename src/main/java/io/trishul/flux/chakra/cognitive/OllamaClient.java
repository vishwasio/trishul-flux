package io.trishul.flux.chakra.cognitive;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Component
public class OllamaClient {

    private final RestClient restClient;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public OllamaClient() {
        this.restClient = RestClient.create();
    }

    // using stream: false to get a single consolidated response.
    public String chat(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                    "model", "qwen2.5-coder:0.5b",
                    "prompt", prompt,
                    "stream", false
            );

            log.info("Control Plane: Sending request to 0.5B model...");

            OllamaResponse response = restClient.post()
                    .uri(OLLAMA_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(OllamaResponse.class);

            return (response != null) ? response.response() : "No response from AI";
        } catch (Exception e) {
            log.error("Control Plane: AI Connection failed. Ensure Ollama is running. Error: {}", e.getMessage());
            return "AI_OFFLINE";
        }
    }

    // Simple record to map Ollama's response JSON
    private record OllamaResponse(String response) {}
}