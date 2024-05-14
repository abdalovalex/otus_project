package org.example.purchaseservice.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BankTransactionEvent {
    private String account;
    private BigDecimal amount;
    private String hash;
    private Type type;
    private Integer demandId;
}
