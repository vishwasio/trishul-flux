package io.trishul.flux.core.telemetry;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class TelemetryRepository {

    // store the last 10 snapshots
    private final LinkedBlockingDeque<TelemetrySnapshot> window = new LinkedBlockingDeque<>(10);

    public void addSnapshot(TelemetrySnapshot snapshot) {
        if (window.size() == 10) {
            window.pollLast(); // Remove oldest
        }
        window.offerFirst(snapshot); // Add newest
    }

    // return the current history as a list for the AI to analyze.
    public List<TelemetrySnapshot> getHistory() {
        return new ArrayList<>(window);
    }

    public int getRecentCount() {
        return window.size();
    }
}