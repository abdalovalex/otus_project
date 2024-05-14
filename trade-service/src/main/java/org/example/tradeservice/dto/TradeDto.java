package org.example.tradeservice.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.tradeservice.entity.StateTrade;

@Builder
@Getter
@Setter
public class TradeDto {
    private Integer id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StateTrade stateTrade;
}
