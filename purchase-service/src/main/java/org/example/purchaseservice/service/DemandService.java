package org.example.purchaseservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.example.purchaseservice.dto.DemandDto;
import org.example.purchaseservice.entity.Demand;
import org.example.purchaseservice.entity.Purchase;
import org.example.purchaseservice.entity.StateDemand;
import org.example.purchaseservice.event.BankTransactionEvent;
import org.example.purchaseservice.event.DemandEvent;
import org.example.purchaseservice.event.PurchaseEvent;
import org.example.purchaseservice.event.State;
import org.example.purchaseservice.event.Type;
import org.example.purchaseservice.exception.PurchaseException;
import org.example.purchaseservice.mapper.DemandMapper;
import org.example.purchaseservice.repository.DemandRepository;
import org.example.purchaseservice.repository.PurchaseRepository;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class DemandService {
    private final StreamBridge streamBridge;
    private final PurchaseRepository purchaseRepository;
    private final DemandRepository demandRepository;
    private final DemandMapper demandMapper;
    private final TransactionTemplate transactionTemplate;

    public Integer create(DemandDto demandDto) throws PurchaseException {
        Purchase purchase = purchaseRepository.findById(demandDto.getPurchaseId())
                .orElseThrow(() -> new PurchaseException("Закупка не найдена"));

        Demand demand = transactionTemplate.execute(status -> {
            BigDecimal amount = BigDecimal.valueOf(10)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    .multiply(purchase.getPrice());

            Demand newDemand = demandMapper.map(demandDto);
            newDemand.setPublicationDateTime(LocalDateTime.now());
            newDemand.setPurchase(purchase);
            newDemand.setAccount(demandDto.getAccount());
            newDemand.setAmount(amount);
            demandRepository.save(newDemand);

            return newDemand;
        });

        if (demand == null) {
            throw new PurchaseException("Не удалось создать заявку");
        }

        BankTransactionEvent bankTransactionEvent = BankTransactionEvent.builder()
                .account(demand.getAccount())
                .amount(demand.getAmount())
                .hash(UUID.randomUUID().toString())
                .type(Type.BLOCK)
                .demandId(demand.getId())
                .build();

        streamBridge.send("bankTransaction",
                          MessageBuilder.withPayload(bankTransactionEvent).build());

        return demand.getId();
    }

    public void demandRepliesHandler(DemandEvent demandEvent) {
        Demand demand = demandRepository.findById(demandEvent.getDemandId()).orElse(null);
        if (demand == null) {
            return;
        }

        if (State.APPROVED.equals(demandEvent.getState())) {
            demand.setState(StateDemand.SUBMITTED);
        } else {
            demand.setState(StateDemand.RETURNED);
        }

        demandRepository.save(demand);
    }

    public void processDemand(PurchaseEvent purchaseEvent) {
        Purchase purchase = purchaseRepository.findByIdWithDemands(purchaseEvent.getPurchaseId()).orElse(null);
        if (purchase == null) {
            return;
        }

        purchase.getDemands().forEach(demand -> {
            if (demand.getId().equals(purchaseEvent.getDemandId())) {
                demand.setWin(true);
                demandRepository.save(demand);
            }

            BankTransactionEvent bankTransactionEvent = BankTransactionEvent.builder()
                    .account(demand.getAccount())
                    .amount(demand.getAmount())
                    .hash(UUID.randomUUID().toString())
                    .type(Type.UNBLOCK)
                    .demandId(demand.getId())
                    .build();

            streamBridge.send("bankTransaction",
                              MessageBuilder.withPayload(bankTransactionEvent).build());

            if (Boolean.TRUE.equals(demand.getWin())) {
                bankTransactionEvent = BankTransactionEvent.builder()
                        .account(demand.getAccount())
                        .amount(demand.getAmount())
                        .hash(UUID.randomUUID().toString())
                        .type(Type.PAYMENT)
                        .demandId(demand.getId())
                        .build();

                streamBridge.send("bankTransaction",
                                  MessageBuilder.withPayload(bankTransactionEvent).build());
            }
        });
    }
}
