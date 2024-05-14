package org.example.purchaseservice.config;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.example.purchaseservice.event.PurchaseEvent;
import org.example.purchaseservice.service.DemandService;
import org.example.purchaseservice.service.PurchaseService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PurchaseRepliesConsumer {
    private final PurchaseService purchaseService;
    private final DemandService demandService;

    @Bean
    public Consumer<PurchaseEvent> purchaseRepliesHandler() {
        return this::accept;
    }

    private void accept(PurchaseEvent purchaseEvent) {
        boolean result = purchaseService.purchaseRepliesHandler(purchaseEvent);
        if (result) {
            demandService.processDemand(purchaseEvent);
        }
    }
}
