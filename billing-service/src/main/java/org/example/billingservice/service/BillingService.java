package org.example.billingservice.service;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billingservice.dto.CreateAccountDTO;
import org.example.billingservice.dto.IncomeMoneyDTO;
import org.example.billingservice.entity.Account;
import org.example.billingservice.entity.Operation;
import org.example.billingservice.entity.State;
import org.example.billingservice.entity.Type;
import org.example.billingservice.event.BankTransactionEvent;
import org.example.billingservice.event.DemandEvent;
import org.example.billingservice.event.NotificationEvent;
import org.example.billingservice.example.AccountException;
import org.example.billingservice.mapper.AccountMapper;
import org.example.billingservice.mapper.OperationMapper;
import org.example.billingservice.repository.AccountRepository;
import org.example.billingservice.repository.OperationRepository;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {
    private final AccountMapper accountMapper;
    private final OperationMapper operationMapper;
    private final StreamBridge streamBridge;
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    public void create(CreateAccountDTO createAccountDTO) throws AccountException {
        if (accountRepository.existsByAccount(createAccountDTO.getAccount())) {
            throw new AccountException("Счет уже создан");
        }
        Account account = accountMapper.map(createAccountDTO);
        accountRepository.save(account);
    }

    public void income(IncomeMoneyDTO incomeMoneyDTO) throws AccountException {
        accountRepository.findByAccount(incomeMoneyDTO.getAccount())
                .orElseThrow(() -> new AccountException("Счет не найден"));

        if (operationRepository.existsByHash(incomeMoneyDTO.getHash())) {
            return;
        }

        BankTransactionEvent bankTransactionEvent = BankTransactionEvent.builder()
                .account(incomeMoneyDTO.getAccount())
                .amount(incomeMoneyDTO.getAmount())
                .hash(incomeMoneyDTO.getHash())
                .type(Type.INFLOW)
                .build();

        streamBridge.send("bankTransaction", MessageBuilder.withPayload(bankTransactionEvent).build());
    }

    public void process(BankTransactionEvent event) {
        Account account = accountRepository.findByAccount(event.getAccount()).orElse(null);
        if (account == null) {
            return;
        }

        try {
            Operation operation = transactionTemplate.execute(status -> {
                if (operationRepository.existsByHash(event.getHash())) {
                    return null;
                }

                Operation newOperation = operationMapper.map(event);
                newOperation.setHash(event.getHash());
                newOperation.setType(event.getType());
                newOperation.setAmount(event.getAmount());
                newOperation.setAccount(account);
                newOperation.setDemandId(event.getDemandId());

                switch (event.getType()) {
                    case INFLOW:
                    case UNBLOCK: {
                        account.setBalance(account.getBalance().add(newOperation.getAmount()));
                        accountRepository.save(account);

                        newOperation.setState(State.APPROVED);
                        operationRepository.save(newOperation);
                        break;
                    }

                    case PAYMENT:
                    case BLOCK: {
                        BigDecimal balance = account.getBalance().subtract(newOperation.getAmount());
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            newOperation.setState(State.REJECTED);
                        } else {
                            account.setBalance(balance);
                            accountRepository.save(account);

                            newOperation.setState(State.APPROVED);
                        }

                        operationRepository.save(newOperation);
                        break;
                    }
                    default:
                        newOperation.setState(State.REJECTED);
                        operationRepository.save(newOperation);
                        break;
                }

                return newOperation;
            });

            if (operation != null) {
                if (!Type.INFLOW.equals(operation.getType())) {
                    DemandEvent demandEvent = DemandEvent.builder()
                            .demandId(operation.getDemandId())
                            .state(operation.getState())
                            .build();

                    streamBridge.send("demandReplies", MessageBuilder.withPayload(demandEvent).build());
                }

                String text = State.APPROVED.equals(operation.getState()) ?
                        "Операция прошла успешно" : "Операция прошла не успешно";
                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .users(Set.of(account.getUserId()))
                        .text(text)
                        .build();
                streamBridge.send("notification", MessageBuilder.withPayload(notificationEvent).build());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
