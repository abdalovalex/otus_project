package org.example.tradeservice.config;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import org.example.tradeservice.event.TradeEvent;
import org.example.tradeservice.service.TradeService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TradeConsumer {
    private final TradeService tradeService;

    @Bean
    public Consumer<TradeEvent> tradeEvent() {
        return tradeService::createTrade;
    }
}
