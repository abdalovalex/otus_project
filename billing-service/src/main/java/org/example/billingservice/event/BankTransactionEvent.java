package org.example.billingservice.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.billingservice.entity.Type;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransactionEvent {
    private String account;
    private BigDecimal amount;
    private String hash;
    private Type type;
    private Integer demandId;
}
