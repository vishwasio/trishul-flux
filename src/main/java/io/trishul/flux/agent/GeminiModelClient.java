package io.trishul.flux.agent;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiModelClient {

    private final RestClient restClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public GeminiModelClient() {
        this.restClient = RestClient.create();
    }

    public String chat(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.0,
                            "maxOutputTokens", 15
                    )
            );

            GeminiResponse response = restClient.post()
                    .uri(GEMINI_URL + "?key=" + apiKey)
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                Candidate firstCandidate = response.candidates().get(0);
                if (firstCandidate.content() != null && firstCandidate.content().parts() != null) {
                    return firstCandidate.content().parts().get(0).text().trim();
                }
            }

            log.warn("[GeminiClient] API returned empty response (Possible safety filter).");
            return "THROTTLE";

        } catch (Exception e) {
            log.error("[GeminiClient] Cloud reasoning failed: {}", e.getMessage());
            return "THROTTLE";
        }
    }

    private record GeminiResponse(List<Candidate> candidates) {}
    private record Candidate(Content content) {}
    private record Content(List<Part> parts) {}
    private record Part(String text) {}
}