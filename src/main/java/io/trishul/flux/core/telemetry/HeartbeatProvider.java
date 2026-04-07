package io.trishul.flux.core.telemetry;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HeartbeatProvider {

    // Listener to confirm system is alive after startup
    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        log.info("auto-infra-resilience-engine [Trishul-Flux]: Kernel Heartbeat Initiated / System is alive.");
    }
}