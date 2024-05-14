package org.example.purchaseservice.config;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.example.purchaseservice.event.DemandEvent;
import org.example.purchaseservice.service.DemandService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DemandRepliesConsumer {
    private final DemandService demandService;

    @Bean
    public Consumer<DemandEvent> demandRepliesHandler() {
        return demandService::demandRepliesHandler;
    }
}
