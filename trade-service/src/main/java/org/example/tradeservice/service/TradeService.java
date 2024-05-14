package org.example.tradeservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tradeservice.dto.TradeDto;
import org.example.tradeservice.dto.UserDto;
import org.example.tradeservice.endpoints.PurchaseClient;
import org.example.tradeservice.entity.Bid;
import org.example.tradeservice.entity.StateTrade;
import org.example.tradeservice.entity.Trade;
import org.example.tradeservice.event.NotificationEvent;
import org.example.tradeservice.event.PurchaseEvent;
import org.example.tradeservice.event.TradeEvent;
import org.example.tradeservice.exception.TradeException;
import org.example.tradeservice.mapper.TradeMapper;
import org.example.tradeservice.repository.TradeRepository;
import org.jetbrains.annotations.NotNull;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {
    private final StreamBridge streamBridge;
    private final TransactionTemplate transactionTemplate;
    private final TradeRepository tradeRepository;
    private final PurchaseClient purchaseClient;
    private final TradeMapper tradeMapper;

    @Transactional
    public void createTrade(TradeEvent tradeEvent) {
        Trade trade = new Trade();
        trade.setPurchaseId(tradeEvent.getPurchaseId());
        trade.setStartDate(tradeEvent.getStartDate());
        trade.setEndDate(tradeEvent.getEndDate());

        tradeRepository.save(trade);
    }

    public TradeDto getTradeByPurchase(Integer purchaseId) throws TradeException {
        Trade trade = tradeRepository.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new TradeException("Торги не найдены"));

        return tradeMapper.map(trade);
    }

    public void startTrade() {
        List<Trade> trades = tradeRepository.findStartTrade();
        trades.forEach(trade -> {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                    trade.setStateTrade(StateTrade.PROCESS);
                    tradeRepository.save(trade);
                }
            });

            List<UserDto> supplierUsers = purchaseClient.getSupplierUsers(trade.getPurchaseId());

            String text = "Торги %d начались".formatted(trade.getPurchaseId());
            Set<Integer> ids = supplierUsers.stream().map(UserDto::getUserId).collect(Collectors.toSet());

            if (!ids.isEmpty()) {
                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .users(ids)
                        .text(text)
                        .build();
                streamBridge.send("notification", MessageBuilder.withPayload(notificationEvent).build());
            }
        });
    }

    public void endTrade() {
        List<Trade> trades = tradeRepository.findEndTrade();
        trades.forEach(trade -> {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                    trade.setStateTrade(StateTrade.END);
                    tradeRepository.save(trade);
                }
            });

            Set<Integer> ids = trade.getBids()
                    .stream()
                    .map(Bid::getUserId)
                    .collect(Collectors.toSet());

            String text;
            if (ids.isEmpty()) {
                text = "Торги %d не состоялись";
            } else {
                text = "Торги %d закончены";
            }

            Bid bid = trade.getBids().stream().min(Comparator.comparing(Bid::getPrice)).orElse(null);

            if (bid != null) {
                PurchaseEvent purchaseEvent = PurchaseEvent.builder()
                        .purchaseId(trade.getPurchaseId())
                        .demandId(bid.getDemandId())
                        .build();
                streamBridge.send("purchaseReplies", MessageBuilder.withPayload(purchaseEvent).build());

                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .users(ids)
                        .text(text.formatted(trade.getPurchaseId()))
                        .build();
                streamBridge.send("notification", MessageBuilder.withPayload(notificationEvent).build());
            } else {
                PurchaseEvent purchaseEvent = PurchaseEvent.builder()
                        .purchaseId(trade.getPurchaseId())
                        .build();
                streamBridge.send("purchaseReplies", MessageBuilder.withPayload(purchaseEvent).build());
            }
        });
    }
}
