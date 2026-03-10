package io.trishul.flux.core.telemetry;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HeartbeatProvider {

    // Listener to confirm Trishul-Flux is alive after startup
    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        log.info("Trishul-Flux: Kernel heartbeat initiated.");
    }
}