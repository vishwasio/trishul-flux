package io.trishul.flux.agent;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Component
public class ModelClient {

    private final RestClient restClient;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "qwen2.5-coder:0.5b";

    public ModelClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5 seconds to connect
        factory.setReadTimeout(60000);    // 60 seconds to "think" - critical for 0.5B model

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public String chat(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL_NAME,
                    "prompt", prompt,
                    "stream", false
            );

            log.info("[ModelClient] Consulting AI (Waiting for reasoning)...");

            OllamaResponse response = restClient.post()
                    .uri(OLLAMA_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response != null && response.response() != null) {
                return response.response().trim();
            }

            return "THROTTLE";

        } catch (Exception e) {
            // this catches the ReadTimeoutException specifically
            log.warn("[ModelClient] AI Reasoning stalled (CPU Contention). Error: {}", e.getMessage());
            return "THROTTLE";
        }
    }

    private record OllamaResponse(String response) {}
}