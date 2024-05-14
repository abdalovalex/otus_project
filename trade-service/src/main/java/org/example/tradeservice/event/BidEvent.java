package org.example.tradeservice.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BidEvent {
    private Integer id;
    private Integer userId;
    private Integer demandId;
    private Integer tradeId;
    private BigDecimal price;
}
