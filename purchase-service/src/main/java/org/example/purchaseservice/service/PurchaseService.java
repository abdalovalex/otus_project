package org.example.purchaseservice.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.purchaseservice.dto.PurchaseDto;
import org.example.purchaseservice.dto.UserDto;
import org.example.purchaseservice.dto.WinnerDto;
import org.example.purchaseservice.endpoints.UserClient;
import org.example.purchaseservice.entity.Demand;
import org.example.purchaseservice.entity.Purchase;
import org.example.purchaseservice.entity.StatePurchase;
import org.example.purchaseservice.event.NotificationEvent;
import org.example.purchaseservice.event.PurchaseEvent;
import org.example.purchaseservice.event.TradeEvent;
import org.example.purchaseservice.exception.PurchaseException;
import org.example.purchaseservice.mapper.PurchaseMapper;
import org.example.purchaseservice.mapper.WinnerMapper;
import org.example.purchaseservice.repository.PurchaseRepository;
import org.jetbrains.annotations.NotNull;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final StreamBridge streamBridge;
    private final PurchaseRepository purchaseRepository;
    private final TransactionTemplate transactionTemplate;
    private final UserClient userClient;
    private final PurchaseMapper purchaseMapper;
    private final WinnerMapper winnerMapper;

    public Integer create(PurchaseDto purchaseDto) throws PurchaseException {
        Purchase purchase = transactionTemplate.execute(status -> {
            Purchase newPurchase = new Purchase();
            newPurchase.setUserId(purchaseDto.getUserId());
            newPurchase.setTitle(purchaseDto.getTitle());
            newPurchase.setPrice(purchaseDto.getPrice());
            newPurchase.setPublicationDateTime(purchaseDto.getPublicationDateTime());
            newPurchase.setDemandEndDateTime(purchaseDto.getDemandEndDateTime());

            purchaseRepository.save(newPurchase);

            return newPurchase;
        });

        if (purchase == null) {
            throw new PurchaseException("Не удалось создать закупку");
        }

        try {
            List<UserDto> users = userClient.getSupplierUsers();

            Set<Integer> ids = users
                    .stream()
                    .map(UserDto::getUserId)
                    .collect(Collectors.toSet());

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .users(ids)
                    .text("Опубликована закупка %d".formatted(purchase.getId()))
                    .build();
            streamBridge.send("notification",
                              MessageBuilder.withPayload(notificationEvent).build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return purchase.getId();
    }

    public PurchaseDto get(Integer purchaseId) throws PurchaseException {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Закупка не найдена"));

        return purchaseMapper.map(purchase);
    }

    @Transactional()
    public List<UserDto> suppliers(Integer purchaseId) throws PurchaseException {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Закупка не найдена"));

        return purchase.getDemands().stream()
                .map(demand -> UserDto.builder().userId(demand.getUserId()).build())
                .toList();
    }

    public void endDemand() {
        List<Purchase> purchases = purchaseRepository.findByEndDemand();

        purchases.forEach(purchase -> {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                    purchase.setState(StatePurchase.TRADE_START);
                    purchaseRepository.save(purchase);
                }
            });

            TradeEvent tradeEvent = TradeEvent.builder().purchaseId(purchase.getId())
                    .startDate(purchase.getDemandEndDateTime())
                    .endDate(purchase.getDemandEndDateTime().plusSeconds(10))
                    .build();

            streamBridge.send("trade",
                              MessageBuilder.withPayload(tradeEvent).build());
        });
    }

    @Transactional
    public boolean purchaseRepliesHandler(PurchaseEvent purchaseEvent) {
        Purchase purchase = purchaseRepository.findById(purchaseEvent.getPurchaseId()).orElse(null);

        if (purchase == null) {
            return false;
        }

        purchase.setState(StatePurchase.FINISH);
        purchaseRepository.save(purchase);

        return true;
    }

    public WinnerDto getWinner(Integer purchaseId) throws PurchaseException {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseException("Закупка не найдена"));

        Demand demand = purchase.getDemands().stream()
                .filter(item -> Boolean.TRUE.equals(item.getWin()))
                .findFirst()
                .orElseThrow(() -> new PurchaseException("Победитель не найден"));

        return winnerMapper.map(demand);
    }
}
