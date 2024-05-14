package org.example.billingservice.config;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billingservice.event.BankTransactionEvent;
import org.example.billingservice.service.BillingService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AccountConfig {
    private final BillingService billingService;

    @Bean
    public Consumer<BankTransactionEvent> bankTransactionHandler() {
        return billingService::process;
    }
}
