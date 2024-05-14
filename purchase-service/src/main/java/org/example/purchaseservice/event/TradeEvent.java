package org.example.purchaseservice.event;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TradeEvent {
    private Integer purchaseId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
