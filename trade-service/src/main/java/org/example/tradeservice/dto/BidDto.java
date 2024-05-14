package org.example.tradeservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BidDto {
    private Integer userId;
    private Integer demandId;
    private Integer tradeId;
    private BigDecimal price;
}
