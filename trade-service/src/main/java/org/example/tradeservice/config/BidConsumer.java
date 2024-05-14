package org.example.tradeservice.config;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.example.tradeservice.event.BidEvent;
import org.example.tradeservice.service.BidService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BidConsumer {
    private final BidService bidService;

    @Bean
    public Consumer<BidEvent> bidProcess() {
        return bidService::bidProcess;
    }
}
